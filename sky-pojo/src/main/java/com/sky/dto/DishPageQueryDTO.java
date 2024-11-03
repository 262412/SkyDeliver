package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO implements Serializable {

    // 页码
    @ApiModelProperty(value = "页码", required = true)
    private int page;

    // 每页记录数
    @ApiModelProperty(value = "每页记录数", required = true)
    private int pageSize;

    // 菜品名称
    @ApiModelProperty(value = "菜品名称", required = false)
    private String name;

    // 分类id
    @ApiModelProperty(value = "分类id", required = false)
    private Integer categoryId;

    // 状态 0表示禁用 1表示启用
    @ApiModelProperty(value = "状态 0表示禁用 1表示启用", required = false)
    private Integer status;

}
