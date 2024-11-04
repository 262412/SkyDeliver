package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/addressBook")
@Slf4j
@Api(value = "用户地址管理接口", tags = "用户地址管理接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @ApiOperation(value  = "新增地址", notes = "新增地址")
    public Result save(AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }
}
