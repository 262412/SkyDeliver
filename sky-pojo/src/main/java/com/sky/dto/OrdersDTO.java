package com.sky.dto;

import com.sky.entity.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersDTO implements Serializable {

    // 订单ID
    @ApiModelProperty(value = "订单ID", required = false)
    private Long id;

    // 订单号
    @ApiModelProperty(value = "订单号", required = true)
    private String number;

    // 订单状态 1: 待付款, 2: 待派送, 3: 已派送, 4: 已完成, 5: 已取消
    @ApiModelProperty(value = "订单状态 (1: 待付款, 2: 待派送, 3: 已派送, 4: 已完成, 5: 已取消)", required = true)
    private Integer status;

    // 下单用户ID
    @ApiModelProperty(value = "下单用户ID", required = true)
    private Long userId;

    // 地址ID
    @ApiModelProperty(value = "地址ID", required = true)
    private Long addressBookId;

    // 下单时间
    @ApiModelProperty(value = "下单时间", required = true)
    private LocalDateTime orderTime;

    // 结账时间
    @ApiModelProperty(value = "结账时间", required = false)
    private LocalDateTime checkoutTime;

    // 支付方式 1: 微信, 2: 支付宝
    @ApiModelProperty(value = "支付方式 (1: 微信, 2: 支付宝)", required = true)
    private Integer payMethod;

    // 实收金额
    @ApiModelProperty(value = "实收金额", required = true)
    private BigDecimal amount;

    // 备注
    @ApiModelProperty(value = "备注", required = false)
    private String remark;

    // 用户名
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    // 手机号
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    // 地址
    @ApiModelProperty(value = "地址", required = true)
    private String address;

    // 收货人
    @ApiModelProperty(value = "收货人", required = true)
    private String consignee;

    // 订单详情列表
    @ApiModelProperty(value = "订单详情列表", required = true)
    private List<OrderDetail> orderDetails;

}
