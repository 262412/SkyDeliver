package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工登录时传递的数据模型")
public class EmployeeLoginDTO implements Serializable {

    // 用户名
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    // 密码
    @ApiModelProperty(value = "密码", required = true)
    private String password;

}
