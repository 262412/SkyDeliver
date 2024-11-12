package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据指定日期范围生成营业额报告
     * 该方法计算从开始日期到结束日期每天的营业额，并将结果封装在TurnoverReportVO对象中返回
     *
     * @param begin 开始日期，表示报告周期的起始点
     * @param end 结束日期，表示报告周期的结束点
     * @return TurnoverReportVO 包含每日营业额信息的报告对象
     */
    @Override
    public TurnoverReportVO turnOverStatics(LocalDate begin, LocalDate end) {
        // 初始化日期列表，用于存储报告周期内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        // 循环添加日期到dateList，直到开始日期等于结束日期
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 初始化营业额列表，用于存储每日营业额
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            // 获取当前日期的开始和结束时间点，用于查询当日订单
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 创建查询参数Map，存放查询条件
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            // 根据订单状态和下单时间查询当日营业额，并处理可能的空值情况
            Double turnover = orderMapper.sumByStatusAndOrderTimeLT(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        // 构建并返回营业额报告VO对象，将日期列表和营业额列表转换为字符串形式
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计报告服务方法
     * 根据指定的开始和结束日期，统计每日的新增用户数和累计用户数
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 返回包含日期、每日新增用户数和累计用户数的用户统计报告对象
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 初始化日期列表，用于存放从开始到结束日期之间的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        // 循环遍历，生成从开始到结束日期之间的所有日期列表
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 初始化新增用户数和累计用户数列表，用于存放每日的新增用户数和累计用户数
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        // 遍历每个日期，统计每日的新增用户数和累计用户数
        for (LocalDate date : dateList) {
            // 获取当前日期的开始和结束时间点，用于查询当日新增用户数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 创建查询参数Map，存放查询条件
            Map map = new HashMap<>();
            map.put("end", endTime);
            // 查询当前日期的累计用户数
            Integer totalUser = userMapper.countUserByMap(map);
            map.put("begin", beginTime);
            // 查询当前日期的新增用户数
            Integer newUser = userMapper.countUserByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        // 构建并返回用户统计报告对象
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 获取指定时间段内的订单统计信息
     *
     * @param begin 开始日期，不包括时间部分
     * @param end 结束日期，不包括时间部分
     * @return 返回一个包含订单统计信息的OrderReportVO对象
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 初始化日期列表，用于存储从开始到结束日期之间的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        // 循环增加日期，直到达到结束日期
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 初始化订单数量和有效订单数量列表
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        // 遍历每个日期，计算当天的订单数量和有效订单数量
        for (LocalDate date : dateList) {
            // 获取当天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 计算当天的订单数量（不考虑订单状态）
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            // 计算当天的有效订单数量（订单状态为已完成）
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        // 计算总订单数量和有效订单数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 初始化订单完成率
        Double orderCompletionRate = 0.0;
        // 如果总订单数量不为0，计算订单完成率
        if (totalOrderCount!= 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        // 构建并返回订单统计报告对象
        OrderReportVO orderReportVO = OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}
