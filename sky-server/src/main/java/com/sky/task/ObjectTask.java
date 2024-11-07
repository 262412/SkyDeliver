package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ObjectTask {
    @Autowired
    private OrderMapper orderMapper;
    // 定时处理超时订单，每小时的第0分钟执行一次
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder(){
        // 记录处理超时订单的日志
        log.info("定时处理超时订单{}", LocalDateTime.now());
        // 获取当前时间前15分钟的时间点
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(15);
        // 查询所有未支付且订单时间小于当前时间前15分钟的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,localDateTime);
        // 如果查询到的订单列表不为空且不为null
        if(ordersList != null && !ordersList.isEmpty()){
            // 遍历订单列表
            for (Orders orders : ordersList) {
                // 设置订单状态为已取消
                orders.setStatus(Orders.CANCELLED);
                // 设置取消原因为订单超时，自动取消
                orders.setCancelReason("订单超时，自动取消");
                // 设置取消时间为当前时间
                orders.setCancelTime(LocalDateTime.now());
                // 更新订单信息
                orderMapper.update(orders);
            }
        }
    }

    // 定时处理处于派送中状态的订单，每月的每一天凌晨1点执行一次
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder(){
        // 记录处理派送中订单的日志
        log.info("定时处理处于派送中状态的订单{}", LocalDateTime.now());
        // 获取当前时间前60分钟的时间点
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(60);
        // 查询所有派送中且订单时间小于当前时间前60分钟的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,localDateTime);
        // 如果查询到的订单列表不为空且不为null
        if(ordersList != null && !ordersList.isEmpty()){
            // 遍历订单列表
            for (Orders orders : ordersList) {
                // 设置订单状态为已完成
                orders.setStatus(Orders.COMPLETED);
                // 更新订单信息
                orderMapper.update(orders);
            }
        }
    }
}
