package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Api(value = "分类接口", tags = "分类接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    @ApiOperation(value = "分类查询", notes = "分类查询")
    public Result<List<Category>> list(Integer type) {
        log.info("分类查询");
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}