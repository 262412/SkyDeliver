package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class OrdersPaymentDTO implements Serializable {

    // 订单号
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNumber;

    // 付款方式 1: 微信, 2: 支付宝
    @ApiModelProperty(value = "付款方式 (1: 微信, 2: 支付宝)", required = true)
    private Integer payMethod;

}
