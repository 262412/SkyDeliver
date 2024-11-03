package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    // 员工ID
    @ApiModelProperty(value = "员工ID", required = false)
    private Long id;

    // 用户名
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    // 姓名
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    // 电话号码
    @ApiModelProperty(value = "电话号码", required = false)
    private String phone;

    // 性别
    @ApiModelProperty(value = "性别", required = false)
    private String sex;

    // 身份证号
    @ApiModelProperty(value = "身份证号", required = true)
    private String idNumber;

}
