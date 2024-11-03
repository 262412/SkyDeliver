package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOverViewQueryDTO implements Serializable {

    // 开始时间
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime begin;

    // 结束时间
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime end;

}
