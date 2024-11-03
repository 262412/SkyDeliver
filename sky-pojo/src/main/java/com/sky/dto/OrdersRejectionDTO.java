package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class OrdersRejectionDTO implements Serializable {

    // 订单ID
    @ApiModelProperty(value = "订单ID", required = true)
    private Long id;

    // 订单拒绝原因
    @ApiModelProperty(value = "订单拒绝原因", required = true)
    private String rejectionReason;

}
