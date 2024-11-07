package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void save(Orders orders);

    Orders getByNumber(String outTradeNo);

    void update(Orders orders);

    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime localDateTime);
}