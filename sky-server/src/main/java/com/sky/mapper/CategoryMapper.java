package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    void save(Category category);

    void deleteById(Long id);

    void update(Category category);

    List<Category> list(Integer type);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
