package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EditPasswordDTO implements Serializable {

    // 员工ID
    @ApiModelProperty(value = "员工ID", required = true)
    private Long empId;

    // 旧密码
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    // 新密码
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

}
