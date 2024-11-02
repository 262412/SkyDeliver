package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 套餐服务实现类
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 保存套餐信息及其关联的菜品信息
     * @param setmealDTO 套餐DTO，包含套餐基本信息及其关联的菜品信息
     * @throws RuntimeException 如果保存过程中出现异常
     */
    @Override
    @Transactional
    public void saveWithFlavor(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.saveWithFlavor(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 批量删除套餐
     * @param ids 待删除的套餐ID列表
     * @throws DeletionNotAllowedException 如果尝试删除正在销售中的套餐
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(setmealId -> {
            setmealMapper.deleteById(setmealId);
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }

    /**
     * 更新套餐信息及其关联的菜品信息
     * @param setmealDTO 套餐DTO，包含更新后的套餐基本信息及其关联的菜品信息
     * @throws RuntimeException 如果更新过程中出现异常
     */
    @Override
    public void updateWithFlavor(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateWithFlavor(setmeal);
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 启用或停用套餐
     * @param status 套餐的状态，启用或停用
     * @param id 待更新状态的套餐ID
     * @throws DeletionNotAllowedException 如果尝试停用包含正在销售中菜品的套餐
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        if(status == StatusConstant.DISABLE){
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && !dishList.isEmpty()){
                dishList.forEach(dish -> {
                    if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                    }
                });
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO 分页查询DTO，包含查询条件和分页参数
     * @return 分页结果，包含总记录数和套餐信息列表
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        List<SetmealVO> result = page.getResult();
        return new PageResult(page.getTotal(),result);
    }

    /**
     * 根据ID查询套餐及其关联的菜品信息
     * @param id 套餐ID
     * @return 套餐VO，包含套餐基本信息及其关联的菜品信息
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        SetmealVO setmealVO = setmealMapper.getByIdWithDish(id);
        return setmealVO;
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemById(id);
    }


}
