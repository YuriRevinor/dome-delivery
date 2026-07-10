package com.yurirvs.dome.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.constant.StatusConstant;
import com.yurirvs.dome.dto.DishDTO;
import com.yurirvs.dome.dto.DishPageQueryDTO;
import com.yurirvs.dome.entity.Dish;
import com.yurirvs.dome.entity.DishFlavor;
import com.yurirvs.dome.exception.DeletionNotAllowedException;
import com.yurirvs.dome.mapper.DishFlavorMapper;
import com.yurirvs.dome.mapper.DishMapper;
import com.yurirvs.dome.mapper.SetmealDishMapper;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.service.DishService;
import com.yurirvs.dome.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public boolean addDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        int addedDish = dishMapper.addDish(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            int addedFlavorBatch = dishFlavorMapper.addFlavorBatch(flavors);
            if (addedDish == 1 && addedFlavorBatch > 0) {
                return true;
            }
            else {
                return false;
            }
        }

        if (addedDish == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    @Override
    public void deleteDish(List<Long> ids) {
        //判断是否起售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否在套餐中
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }*/

        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Transactional
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        //修改菜品表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.updateById(dish);


        //修改口味表
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            int addedFlavorBatch = dishFlavorMapper.addFlavorBatch(flavors);
        }
    }

    @Override
    public void toggleDishStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {

        return dishMapper.getByCategoryId(categoryId);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
