package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginDTO implements Serializable {

    // 登录码
    @ApiModelProperty(value = "登录码", required = true)
    private String code;

}
