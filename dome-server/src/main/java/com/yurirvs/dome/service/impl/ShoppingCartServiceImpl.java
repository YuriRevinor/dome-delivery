package com.yurirvs.dome.service.impl;

import com.yurirvs.dome.context.BaseContext;
import com.yurirvs.dome.dto.ShoppingCartDTO;
import com.yurirvs.dome.entity.Dish;
import com.yurirvs.dome.entity.Setmeal;
import com.yurirvs.dome.entity.ShoppingCart;
import com.yurirvs.dome.mapper.DishMapper;
import com.yurirvs.dome.mapper.SetmealMapper;
import com.yurirvs.dome.mapper.ShoppingCartMapper;
import com.yurirvs.dome.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        //判断购物车内是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();

        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //in case of already exists
        if (list != null && list.size() > 0) {
            ShoppingCart item = list.get(0);
            item.setNumber(item.getNumber() + 1);
            shoppingCartMapper.updateNumById(item);
            return;
        }

        //添加的为菜品
        if (shoppingCartDTO.getDishId() != null) {
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        }
        //添加的为套餐
        else {
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setNumber(1);

        shoppingCartMapper.insert(shoppingCart);
    }

    @Override
    public List<ShoppingCart> list(Long userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanCart(Long userId) {
        shoppingCartMapper.DeleteByUserId(userId);
    }

    @Override
    public void subFromCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart item = list.get(0);

        if (item.getNumber() == 1) {
            shoppingCartMapper.DeleteById(item.getId());
        }
        else {
            item.setNumber(item.getNumber() - 1);
            shoppingCartMapper.updateNumById(item);
        }

    }
}
