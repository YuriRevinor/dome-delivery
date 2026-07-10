package com.yurirvs.dome.mapper;

import com.github.pagehelper.Page;
import com.yurirvs.dome.annotation.AutoFill;
import com.yurirvs.dome.dto.DishPageQueryDTO;
import com.yurirvs.dome.entity.Dish;
import com.yurirvs.dome.enumeration.OperationType;
import com.yurirvs.dome.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    int addDish(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("SELECT * FROM dish WHERE id=#{id}")
    Dish getById(Long id);

    @Delete("DELETE FROM dish WHERE id=#{id}")
    void deleteById(Long id);

    void deleteByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void updateById(Dish dish);

    @Select("SELECT * FROM dish WHERE category_id=#{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    List<Dish> getBySetmealId(Long SetmealId);
}
