package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.SetmealDTO;
import com.yurirvs.dome.dto.SetmealPageQueryDTO;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteByIds(List<Long> ids);

    SetmealVO getById(Long id);

    void updateSetmealWithDish(SetmealDTO setmealDTO);

    void toggleStatus(Long id, Integer status);
}
