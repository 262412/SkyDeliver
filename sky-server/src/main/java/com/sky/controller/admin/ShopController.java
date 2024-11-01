package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(value = "店铺相关接口", tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String SHOP_STATUS = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @PutMapping("/{status}")
    @ApiOperation(value = "起售停售店铺", notes = "起售停售店铺")
    public Result setStatus(@PathVariable Integer status) {
        log.info("起售停售店铺:{}", status == 1 ? "起售" : "停售");
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation(value = "查询店铺状态", notes = "查询店铺状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("查询店铺状态:{}", status == 1 ? "起售中" : "停售中");
        return Result.success(status);
    }
}
