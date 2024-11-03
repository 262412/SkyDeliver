package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    // 菜品ID
    @ApiModelProperty(value = "菜品ID（与套餐ID互斥）", required = false)
    private Long dishId;

    // 套餐ID
    @ApiModelProperty(value = "套餐ID（与菜品ID互斥）", required = false)
    private Long setmealId;

    // 菜品口味
    @ApiModelProperty(value = "菜品口味", required = false)
    private String dishFlavor;

}
