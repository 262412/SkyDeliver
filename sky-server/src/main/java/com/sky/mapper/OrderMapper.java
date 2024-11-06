package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    void save(Orders orders);

    Orders getByNumber(String outTradeNo);

    void update(Orders orders);
}
