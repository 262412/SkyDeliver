package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("usrShopController")
@RequestMapping("/user/shop")
@Api(value = "店铺相关接口", tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String SHOP_STATUS = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/status")
    @ApiOperation(value = "查询店铺状态", notes = "查询店铺状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("查询店铺状态:{}", status == 1 ? "起售中" : "停售中");
        return Result.success(status);
    }
}
