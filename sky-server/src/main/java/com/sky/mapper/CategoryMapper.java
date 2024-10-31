package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    void save(CategoryDTO categoryDTO);

    void deleteById(Long id);
}
