package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 */
@RestController
@RequestMapping("/admin/category")
@Api(value = "分类相关接口", tags = "分类相关接口")
@Slf4j
public class CategoryController {

    /**
     * 注入分类服务
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO 分类数据传输对象
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增分类", notes = "新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类，分类数据:{}",categoryDTO.toString());
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping
    @ApiOperation(value = "删除分类", notes = "删除分类")
    public Result deleteById(@RequestParam Long id) {
        log.info("删除分类，分类ID为:{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO 分类数据传输对象
     * @return 操作结果
     */
    @PutMapping
    @ApiOperation(value = "修改分类", notes = "修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类，分类数据:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用分类
     *
     * @param status 分类状态
     * @param id 分类ID
     * @return 操作结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用分类", notes = "启用禁用分类")
    public Result startOrStop(@PathVariable Integer status,Long id) {
        log.info("启用禁用分类，状态:{},分类ID:{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 查询分类列表
     *
     * @param type 分类类型
     * @return 分类列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询分类", notes = "查询分类")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        log.info("查询分类，分类类型:{}",type);
        return Result.success(list);
    }

    /**
     * 分页查询分类
     *
     * @param categoryPageQueryDTO 分类分页查询数据传输对象
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询分类", notes = "分页查询分类")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类，分类数据:{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
}
