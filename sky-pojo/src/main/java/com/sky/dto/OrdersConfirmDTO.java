package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersConfirmDTO implements Serializable {

    // 订单ID
    @ApiModelProperty(value = "订单ID", required = true)
    private Long id;

    // 订单状态 1: 待付款, 2: 待接单, 3: 已接单, 4: 派送中, 5: 已完成, 6: 已取消, 7: 退款
    @ApiModelProperty(value = "订单状态 (1: 待付款, 2: 待接单, 3: 已接单, 4: 派送中, 5: 已完成, 6: 已取消, 7: 退款)", required = true)
    private Integer status;

}
