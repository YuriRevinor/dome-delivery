package com.yurirvs.dome.controller.admin;

import com.yurirvs.dome.dto.SetmealDTO;
import com.yurirvs.dome.dto.SetmealPageQueryDTO;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.SetmealService;
import com.yurirvs.dome.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐接口")
@Slf4j
@RequestMapping("/admin/setmeal")
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @CacheEvict(cacheNames = "setmealCache",key = "#{setmealDTO.categoryId}")
    @ApiOperation("添加套餐")
    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐: {}", setmealDTO);

        setmealService.addSetmeal(setmealDTO);

        return Result.success();
    }

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询: {}", setmealPageQueryDTO);

        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @ApiOperation("删除套餐")
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam List<Long> ids) {
        log.info("批量删除套餐: {}", ids);

        setmealService.deleteByIds(ids);

        return Result.success();
    }

    @ApiOperation("根据ID查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据ID查询完整套餐: {}", id);

        SetmealVO setmealVO = setmealService.getById(id);

        return Result.success(setmealVO);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @ApiOperation("更新套餐")
    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("更新套餐: {}", setmealDTO);

        setmealService.updateSetmealWithDish(setmealDTO);

        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @ApiOperation("切换套餐起售/停售")
    @PostMapping("/status/{status}")
    public Result<String> toggleStatus(Long id, @PathVariable Integer status){
        log.info("切换套餐起售/停售: ");

        setmealService.toggleStatus(id,status);

        return Result.success();
    }
}
