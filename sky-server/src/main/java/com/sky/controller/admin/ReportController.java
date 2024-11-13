package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(value = "数据统计相关接口", tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @GetMapping("/turnoverStatics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnOverStatics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        TurnoverReportVO vo = reportService.turnOverStatics(begin, end);
        log.info("营业额统计结果：{}", vo);
        return Result.success(vo);
    }
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        UserReportVO vo = reportService.userStatistics(begin, end);
        log.info("用户统计结果：{}", vo);
        return Result.success(vo);
    }
    @GetMapping("/ordersStatistics(")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        OrderReportVO vo = reportService.getOrderStatistics(begin, end);
        log.info("订单统计结果：{}", vo);
        return Result.success(vo);
    }
    @GetMapping("/top10")
    @ApiOperation("销量排名top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        SalesTop10ReportVO vo = reportService.getOrderTop10(begin, end);
        log.info("销量排名top10:{}", vo);
        return Result.success(vo);
    }
    @GetMapping("/export")
    @ApiOperation("导出数据")
    public void export(HttpServletResponse response) {
        reportService.exportBusinessData(response);
    }
}
