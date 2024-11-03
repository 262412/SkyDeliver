package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class SetmealPageQueryDTO implements Serializable {

    // 当前页码
    @ApiModelProperty(value = "当前页码", required = true)
    private int page;

    // 每页显示的数量
    @ApiModelProperty(value = "每页显示的数量", required = true)
    private int pageSize;

    // 套餐名称
    @ApiModelProperty(value = "套餐名称", required = false)
    private String name;

    // 分类ID
    @ApiModelProperty(value = "分类ID", required = false)
    private Integer categoryId;

    // 状态 0表示禁用, 1表示启用
    @ApiModelProperty(value = "状态 (0表示禁用, 1表示启用)", required = false)
    private Integer status;

}
