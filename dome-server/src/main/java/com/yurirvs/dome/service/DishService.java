package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.DishDTO;
import com.yurirvs.dome.dto.DishPageQueryDTO;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.vo.DishVO;

import java.util.List;

public interface DishService {

    boolean addDishWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    void updateDishWithFlavor(DishDTO dishDTO);

    void toggleDishStatus(Long id, Integer status);
}
