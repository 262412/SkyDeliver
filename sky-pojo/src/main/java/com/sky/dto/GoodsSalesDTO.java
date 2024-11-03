package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsSalesDTO implements Serializable {

    // 商品名称
    @ApiModelProperty(value = "商品名称", required = true)
    private String name;

    // 销量
    @ApiModelProperty(value = "销量", required = true)
    private Integer number;

}
