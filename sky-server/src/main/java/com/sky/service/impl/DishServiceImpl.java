package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 保存菜品信息及其对应的口味
     * 此方法首先将DishDTO中的属性复制到Dish对象中，然后保存Dish对象
     * 如果菜品有口味信息，则为每个口味设置对应的菜品ID，并批量插入口味信息
     *
     * @param dishDTO 菜品数据传输对象，包含菜品基本信息及其对应的口味列表
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 创建一个新的Dish对象
        Dish dish = new Dish();
        // 将DishDTO中的属性复制到Dish对象中
        BeanUtils.copyProperties(dishDTO, dish);
        // 保存Dish对象到数据库
        dishMapper.save(dish);
        // 获取保存后的菜品ID
        Long dishId = dish.getId();
        // 获取菜品的口味列表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 如果口味列表不为空，则遍历每个口味并设置其对应的菜品ID
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            // 批量插入口味信息到数据库
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void deleteById(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException("起售中的菜品不能删除");
            }
        }
        List<Long> setMealIds = setmealDishMapper.getSetmealIdByDishId(ids);
        if (setMealIds != null && !setMealIds.isEmpty()) {
            throw new DeletionNotAllowedException("菜品正在被套餐使用，不能删除");
        }
        for (Long id : ids) {
            dishMapper.deleteById(ids);
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        List<DishVO> result = page.getResult();
        return new PageResult(page.getTotal(), result);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

}

