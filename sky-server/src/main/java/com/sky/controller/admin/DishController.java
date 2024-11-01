package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(value = "菜品管理接口", tags = "菜品管理接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping
    @ApiOperation(value = "新增菜品", notes = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation(value = "删除菜品", notes = "删除菜品")
    public Result deleteById(List<Long> ids) {
        log.info("删除菜品:{}", ids);
        dishService.deleteById(ids);
        return Result.success();
    }
    @PutMapping
    @ApiOperation(value = "修改菜品", notes = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询", notes = "菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
}
