package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoopingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 向购物车添加商品
     * 此方法首先将传入的ShoppingCartDTO对象属性复制到一个新的ShoppingCart对象中，
     * 然后获取当前用户ID并设置到购物车对象中之后，检查购物车中是否已存在商品，
     * 如果存在则更新商品数量，否则根据菜品或套餐ID查询相关信息并添加到购物车中
     *
     * @param shoppingCartDTO 购物车DTO对象，包含要添加到购物车的商品信息
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 创建一个新的ShoppingCart对象
        ShoppingCart shoppingCart = new ShoppingCart();
        // 将ShoppingCartDTO对象属性复制到ShoppingCart对象中
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 设置购物车对象的用户ID
        shoppingCart.setUserId(userId);
        // 根据用户ID查询购物车中的商品列表
        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        // 如果商品列表不为空，则更新第一个商品的数量
        if(list != null && !list.isEmpty()){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        }else {
            // 如果商品列表为空，则根据菜品或套餐ID查询相关信息并添加到购物车中
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null){
                // 如果菜品ID不为空，则查询菜品信息并设置到购物车对象中
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                // 如果菜品ID为空，则查询套餐信息并设置到购物车对象中
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            // 设置购物车对象的数量为1
            shoppingCart.setNumber(1);
            // 设置购物车对象的创建时间为当前时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 将购物车对象添加到数据库中
            shoppingCartMapper.add(shoppingCart);
        }
    }
}
