package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrdersPageQueryDTO implements Serializable {

    // 当前页码
    @ApiModelProperty(value = "当前页码", required = true)
    private int page;

    // 每页显示的数量
    @ApiModelProperty(value = "每页显示的数量", required = true)
    private int pageSize;

    // 订单号
    @ApiModelProperty(value = "订单号", required = false)
    private String number;

    // 手机号
    @ApiModelProperty(value = "手机号", required = false)
    private String phone;

    // 订单状态 1: 待付款, 2: 待派送, 3: 已派送, 4: 已完成, 5: 已取消
    @ApiModelProperty(value = "订单状态 (1: 待付款, 2: 待派送, 3: 已派送, 4: 已完成, 5: 已取消)", required = false)
    private Integer status;

    // 开始时间
    @ApiModelProperty(value = "开始时间", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    // 结束时间
    @ApiModelProperty(value = "结束时间", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    // 用户ID
    @ApiModelProperty(value = "用户ID", required = false)
    private Long userId;

}
