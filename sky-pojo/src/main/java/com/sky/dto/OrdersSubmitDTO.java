package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {

    // 地址簿ID
    @ApiModelProperty(value = "地址簿ID", required = true)
    private Long addressBookId;

    // 付款方式 1: 微信, 2: 支付宝
    @ApiModelProperty(value = "付款方式 (1: 微信, 2: 支付宝)", required = true)
    private int payMethod;

    // 备注
    @ApiModelProperty(value = "备注", required = false)
    private String remark;

    // 预计送达时间
    @ApiModelProperty(value = "预计送达时间", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    // 配送状态 1: 立即送出, 0: 选择具体时间
    @ApiModelProperty(value = "配送状态 (1: 立即送出, 0: 选择具体时间)", required = true)
    private Integer deliveryStatus;

    // 餐具数量
    @ApiModelProperty(value = "餐具数量", required = false)
    private Integer tablewareNumber;

    // 餐具数量状态 1: 按餐量提供, 0: 选择具体数量
    @ApiModelProperty(value = "餐具数量状态 (1: 按餐量提供, 0: 选择具体数量)", required = true)
    private Integer tablewareStatus;

    // 打包费
    @ApiModelProperty(value = "打包费", required = false)
    private Integer packAmount;

    // 总金额
    @ApiModelProperty(value = "总金额", required = true)
    private BigDecimal amount;

}
