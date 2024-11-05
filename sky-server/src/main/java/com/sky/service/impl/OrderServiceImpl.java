package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
