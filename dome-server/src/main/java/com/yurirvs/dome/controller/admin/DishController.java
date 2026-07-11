package com.yurirvs.dome.controller.admin;


import com.yurirvs.dome.dto.DishDTO;
import com.yurirvs.dome.dto.DishPageQueryDTO;
import com.yurirvs.dome.entity.Dish;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.DishService;
import com.yurirvs.dome.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品: {}", dishDTO);

        if (dishService.addDishWithFlavor(dishDTO)) {
            String key = "dish_" + dishDTO.getCategoryId();

            cleanRedis(key);

            return Result.success();
        }
        else {
            return Result.error("添加菜品失败");
        }
    }

    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品: {}", dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    @ApiOperation("删除菜品")
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除菜品: {}", ids);

        dishService.deleteDish(ids);

        cleanRedis("dish_*");

        return Result.success();
    }

    @ApiOperation("根据Id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("ID查询菜品: ", id);

        DishVO dish = dishService.getByIdWithFlavor(id);

        return Result.success(dish);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品: {}", dishDTO);

        dishService.updateDishWithFlavor(dishDTO);

        cleanRedis("dish_*");

        return Result.success();
    }

    @ApiOperation("修改菜品状态")
    @PostMapping("/status/{status}")
    public Result<String> toggleStatus(Long id, @PathVariable Integer status) {
        log.info("修改菜品起售/停售: {},{}", id, status);

        dishService.toggleDishStatus(id, status);

        cleanRedis("dish_*");

        return Result.success();
    }

    @ApiOperation("根据分类查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        log.info("根据分类查询菜品: {}", categoryId);

        List<Dish> dishes = dishService.getByCategoryId(categoryId);

        return Result.success(dishes);
    }

    private void cleanRedis(String patten) {
        Set keys = redisTemplate.keys(patten);

        redisTemplate.delete(keys);
    }
}
