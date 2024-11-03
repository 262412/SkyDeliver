package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {

    // 主键
    @ApiModelProperty(value = "主键", required = false)
    private Long id;

    // 类型 1 菜品分类 2 套餐分类
    @ApiModelProperty(value = "类型 1 菜品分类 2 套餐分类", required = true)
    private Integer type;

    // 分类名称
    @ApiModelProperty(value = "分类名称", required = true)
    private String name;

    // 排序
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

}
