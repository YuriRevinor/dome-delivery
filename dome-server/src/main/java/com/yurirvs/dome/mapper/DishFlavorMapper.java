package com.yurirvs.dome.mapper;

import com.yurirvs.dome.annotation.AutoFill;
import com.yurirvs.dome.entity.DishFlavor;
import com.yurirvs.dome.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    int addFlavorBatch(List<DishFlavor> flavors);

    @Delete("DELETE FROM dish_flavor WHERE dish_id=#{dishId}")
    void deleteByDishId(Long dishId);

    void deleteByDishIds(List<Long> ids);

    @Select("SELECT * FROM dish_flavor WHERE dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
