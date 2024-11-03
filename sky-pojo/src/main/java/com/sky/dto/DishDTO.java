package com.sky.dto;

import com.sky.entity.DishFlavor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO implements Serializable {

    // 菜品id
    @ApiModelProperty(value = "菜品id", required = false)
    private Long id;

    // 菜品名称
    @ApiModelProperty(value = "菜品名称", required = true)
    private String name;

    // 菜品分类id
    @ApiModelProperty(value = "菜品分类id", required = true)
    private Long categoryId;

    // 菜品价格
    @ApiModelProperty(value = "菜品价格", required = true)
    private BigDecimal price;

    // 图片
    @ApiModelProperty(value = "图片", required = false)
    private String image;

    // 描述信息
    @ApiModelProperty(value = "描述信息")
    private String description;

    // 状态 0 停售 1 起售
    @ApiModelProperty(value = "状态", required = true)
    private Integer status;

    // 口味
    @ApiModelProperty(value = "口味")
    private List<DishFlavor> flavors = new ArrayList<>();

}
