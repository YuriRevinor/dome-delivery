package com.yurirvs.dome.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
