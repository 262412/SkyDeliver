package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void save(Dish dish);

    void deleteById(List<Long> ids);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    Dish getById(Long id);

    void update(Dish dish);
}
