package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@Api(value = "分类相关接口", tags = "分类相关接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    @ApiOperation(value = "新增分类", notes = "新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO)
    {
        log.info("新增分类，分类数据:{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation(value = "删除分类", notes = "删除分类")
    public Result deleteById(@RequestParam Long id)
    {
        log.info("删除分类，分类ID为:{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }
    @PutMapping
    @ApiOperation(value = "修改分类", notes = "修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO)
    {
        log.info("修改分类，分类数据:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用分类", notes = "启用禁用分类")
    public Result startOrStop(@PathVariable Integer status,Long id)
    {
        log.info("启用禁用分类，状态:{},分类ID:{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }
}
