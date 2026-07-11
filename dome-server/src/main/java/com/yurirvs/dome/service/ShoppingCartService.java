package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.ShoppingCartDTO;
import com.yurirvs.dome.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addToCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list(Long userId);

    void cleanCart(Long userId);

    void subFromCart(ShoppingCartDTO shoppingCartDTO);
}
