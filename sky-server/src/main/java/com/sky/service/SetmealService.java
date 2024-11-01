package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void saveWithFlavor(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    void updateWithFlavor(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getByIdWithDish(Long id);
}
