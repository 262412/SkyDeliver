package com.sky.service;

import com.sky.dto.CategoryDTO;

public interface CategoryService {
    void save(CategoryDTO categoryDTO);

    void deleteById(Long id);

    void update(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);
}
