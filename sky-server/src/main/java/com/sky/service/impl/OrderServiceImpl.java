package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * 提交订单方法
     *
     * @param ordersSubmitDTO 订单提交数据传输对象，包含提交订单所需的信息
     * @return 返回订单提交结果视图对象
     * @throws AddressBookBusinessException 如果地址簿不存在，则抛出地址簿业务异常
     * @throws ShoppingCartBusinessException 如果购物车为空，则抛出购物车业务异常
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 根据地址簿ID获取地址簿信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        // 如果地址簿为空，抛出异常
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 创建购物车对象并设置用户ID
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);

        // 获取用户购物车列表
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        // 如果购物车为空或不存在，抛出异常
        if(list==null|| list.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 创建订单对象并从DTO复制属性
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);

        // 设置订单相关属性
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        // 保存订单到数据库
        orderMapper.save(orders);

        // 创建订单详情列表
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        // 批量插入订单详情到数据库
        orderDetailMapper.batchInsert(orderDetailList);

        // 删除用户购物车中的商品
        shoppingCartMapper.deleteById(userId);

        // 创建订单提交结果视图对象并设置属性
        OrderSubmitVO orderSubmitVO =  OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        // 返回订单提交结果视图对象
        return orderSubmitVO;
    }

    /**
     * 支付功能实现方法
     * 该方法处理订单支付逻辑，包括调用微信支付接口生成预支付交易单，
     * 并根据返回结果生成订单支付信息对象返回给前端
     *
     * @param ordersPaymentDTO 订单支付数据传输对象，包含订单相关信息
     * @return 返回OrderPaymentVO对象，包含订单支付相关信息
     * @throws Exception 如果支付过程中发生错误，抛出异常
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        // 根据用户ID获取用户信息
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        //检查订单是否已支付
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        //将返回的JSON对象转换为OrderPaymentVO对象
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        //设置package字符串，用于前端调用微信支付
        vo.setPackageStr(jsonObject.getString("package"));

        // 返回订单支付信息对象
        return vo;
    }

    /**
     * 处理支付成功后的逻辑
     * 当支付成功时，此方法被调用，主要用于更新订单状态和发送支付成功消息
     *
     * @param outTradeNo 订单号，用于识别和处理特定的订单
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 更新订单状态为待确认、已支付，并记录结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        // 更新订单状态
        orderMapper.update(orders);

        // 创建一个用于存储消息参数的Map对象
        Map map = new HashMap();

        // 设置消息类型
        map.put("type",1);
        // 设置消息关联的订单ID
        map.put("orderId",ordersDB.getId());
        // 构造消息内容，通知所有客户端订单支付成功
        map.put("content","订单号：" + outTradeNo + "支付成功");

        // 将消息以JSON格式发送给所有客户端
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 重复订单方法
     * 该方法用于将已存在的订单详情转换为购物车对象，并重新插入数据库
     * 主要用于当用户想要重复购买之前订单中的所有商品时
     *
     * @param id 订单ID，用于查询订单详情
     */
    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            // 设置当前用户ID
            shoppingCart.setUserId(userId);
            // 设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 根据用户ID和订单状态进行分页查询
     *
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return 返回分页查询结果，包括总记录数和订单列表
     */
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        // 构造查询条件
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 构造返回的订单列表
        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 根据订单ID获取订单详情
     *
     * 此方法首先根据提供的订单ID查询订单信息，然后查询与该订单关联的菜品或套餐明细，
     * 最后将这些信息封装到一个订单视图对象(OrderVO)中并返回这为前端或其他服务提供详细信息
     *
     * @param id 订单的唯一标识符
     * @return 返回包含订单及其详情的OrderVO对象
     */
    @Override
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 根据用户ID取消订单
     * 此方法首先会根据提供的订单ID查询订单信息，如果订单不存在，则抛出异常
     * 如果订单状态允许取消，则更新订单状态为已取消，并根据订单当前状态决定是否需要退款
     * 对于待接单状态的订单取消时，会调用微信支付退款接口进行退款操作
     *
     * @param id 订单ID，用于识别并操作特定的订单记录
     * @throws Exception 如果订单不存在或订单状态不允许取消，则抛出异常
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        //只有当订单状态为待付款或待接单时，才允许用户取消订单
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    ordersDB.getNumber(), //商户订单号
                    ordersDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

    }

    /**
     * 根据订单ID提醒所有客户端订单已支付
     * 此方法首先会根据提供的订单ID从数据库中获取订单信息如果订单不存在，则抛出异常
     * 接着，构造一个消息映射，包含订单类型、订单ID和订单已支付的信息内容，
     * 并通过WebSocket服务器将此信息发送给所有客户端
     *
     * @param id 订单的唯一标识符如果订单不存在，将抛出OrderBusinessException异常
     */
    @Override
    public void reminder(Long id) {
        // 根据订单ID获取订单信息
        Orders ordersDB = orderMapper.getById(id);
        // 检查订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 构造消息映射
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + ordersDB.getNumber() + "的订单已支付");
        // 将消息发送给所有客户端
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
