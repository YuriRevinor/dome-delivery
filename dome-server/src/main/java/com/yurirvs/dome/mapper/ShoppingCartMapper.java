package com.yurirvs.dome.mapper;

import com.yurirvs.dome.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumById(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    @Delete("DELETE FROM shopping_cart WHERE user_id=#{userId}")
    void DeleteByUserId(Long userId);

    @Delete("DELETE FROM shopping_cart WHERE id=#{id}")
    void DeleteById(Long id);
}
