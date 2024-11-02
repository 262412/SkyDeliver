package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
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

import java.util.ArrayList;
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
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.save(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 批量删除菜品
     * 此方法首先检查菜品的状态和是否关联套餐，如果菜品上架或有关联套餐，则不允许删除
     * 检查通过后，删除菜品及其对应的口味信息
     *
     * @param ids 要删除的菜品ID列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setMealIds = setmealDishMapper.getSetmealIdByDishId(ids);
        if (setMealIds != null && !setMealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 更新菜品信息及其对应的口味
     * 此方法首先更新Dish对象，然后删除原有的口味信息，最后重新插入口味信息
     *
     * @param dishDTO 菜品数据传输对象，包含更新后的菜品基本信息及其对应的口味列表
     */
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

    /**
     * 分页查询菜品信息
     * 此方法使用PageHelper进行分页查询，并返回查询结果
     *
     * @param dishPageQueryDTO 分页查询条件对象，包含页码、页面大小等信息
     * @return 分页结果对象，包含总记录数和查询到的菜品信息列表
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        List<DishVO> result = page.getResult();
        return new PageResult(page.getTotal(), result);
    }

    /**
     * 根据菜品ID查询菜品及其对应的口味信息
     *
     * @param id 菜品ID
     * @return 菜品及其对应的口味信息对象
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 根据分类ID查询上架的菜品列表
     *
     * @param categoryId 分类ID
     * @return 上架的菜品列表
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 启用或禁用菜品
     *
     * @param status 菜品状态，启用或禁用
     * @param id     菜品ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }
    /**
     * 根据菜品条件查询菜品列表，并且包含对应的口味信息
     *
     * @param dish 菜品条件，用于过滤查询结果
     * @return 包含口味信息的菜品列表
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        // 查询符合条件的菜品列表
        List<Dish> dishList = dishMapper.list(dish);
        List<DishVO> dishVOList = new ArrayList<>();

        // 遍历菜品列表，为每个菜品获取其对应的口味信息
        for (Dish d : dishList){
            DishVO dishVO = new DishVO();
            // 将菜品信息复制到菜品VO对象中
            BeanUtils.copyProperties(d,dishVO);
            // 获取当前菜品的ID
            Long dishId = d.getId();
            // 根据菜品ID查询该菜品的所有口味信息
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishId);
            // 将口味信息设置到菜品VO对象中
            dishVO.setFlavors(flavors);
            // 将包含口味信息的菜品VO对象添加到列表中
            dishVOList.add(dishVO);
        }
        // 返回包含口味信息的菜品列表
        return dishVOList;
    }
}
