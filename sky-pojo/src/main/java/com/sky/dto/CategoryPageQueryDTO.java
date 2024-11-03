package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryPageQueryDTO implements Serializable {

    // 页码
    @ApiModelProperty(value = "页码", required = true)
    private int page;

    // 每页记录数
    @ApiModelProperty(value = "每页记录数", required = true)
    private int pageSize;

    // 分类名称
    @ApiModelProperty(value = "分类名称", required = false)
    private String name;

    // 分类类型 1菜品分类 2套餐分类
    @ApiModelProperty(value = "分类类型 1菜品分类 2套餐分类", required = false)
    private Integer type;

}
