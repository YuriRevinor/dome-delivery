package com.yurirvs.dome.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.constant.StatusConstant;
import com.yurirvs.dome.dto.SetmealDTO;
import com.yurirvs.dome.dto.SetmealPageQueryDTO;
import com.yurirvs.dome.entity.Dish;
import com.yurirvs.dome.entity.Setmeal;
import com.yurirvs.dome.entity.SetmealDish;
import com.yurirvs.dome.exception.DeletionNotAllowedException;
import com.yurirvs.dome.exception.SetmealEnableFailedException;
import com.yurirvs.dome.mapper.DishMapper;
import com.yurirvs.dome.mapper.SetmealDishMapper;
import com.yurirvs.dome.mapper.SetmealMapper;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.service.SetmealService;
import com.yurirvs.dome.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.addSetmeal(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }

        setmealDishMapper.addSetMealDishes(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealPageQueryDTO, setmeal);
        if (setmealPageQueryDTO.getCategoryId() != null) {
            setmeal.setCategoryId(setmealPageQueryDTO.getCategoryId().longValue());
        }


        Page<SetmealVO> page = setmealMapper.pageQueryWithCategoryName(setmeal);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        //检查套餐是否停售
        List<Setmeal> setmeals = setmealMapper.getByIds(ids);
        if (setmeals != null || setmeals.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //删除套餐菜品关联
        setmealDishMapper.deleteBySetmealIds(ids);
        //删除套餐
        setmealMapper.deleteByIds(ids);
    }

    @Override
    public SetmealVO getById(Long id) {
        //查询套餐
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        //查询套餐内菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Transactional
    @Override
    public void updateSetmealWithDish(SetmealDTO setmealDTO) {
        //修改套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateSetmeal(setmeal);

        //修改套餐内菜品
        setmealDishMapper.deleteBySetmealIds(Collections.singletonList(setmealDTO.getId()));
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.addSetMealDishes(setmealDishes);
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        List<Dish> dishes = dishMapper.getBySetmealId(id);
        for (Dish dish : dishes) {
            if(dish.getStatus()== StatusConstant.DISABLE){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        setmealMapper.updateSetmeal(setmeal);
    }
}
