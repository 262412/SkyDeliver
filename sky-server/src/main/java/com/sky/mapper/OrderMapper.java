package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void save(Orders orders);

    Orders getByNumber(String outTradeNo);

    void update(Orders orders);

    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime localDateTime);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    Orders getById(Long id);

    Double sumByStatusAndOrderTimeLT(Map map);

    Integer countByMap(Map map);
}
