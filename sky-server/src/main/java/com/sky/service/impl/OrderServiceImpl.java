package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
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

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;

    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
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
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);

    }

}
