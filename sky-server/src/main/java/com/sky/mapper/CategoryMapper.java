package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    void save(CategoryDTO categoryDTO);

    void deleteById(Long id);

    void update(Category category);
}
