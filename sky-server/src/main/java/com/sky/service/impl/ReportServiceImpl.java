package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
}
