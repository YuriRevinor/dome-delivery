package com.yurirvs.dome.mapper;


import com.yurirvs.dome.annotation.AutoFill;
import com.yurirvs.dome.entity.SetmealDish;
import com.yurirvs.dome.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    void addSetMealDishes(List<SetmealDish> setmealDishes);

    void deleteBySetmealIds(List<Long> ids);

    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id= #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}
