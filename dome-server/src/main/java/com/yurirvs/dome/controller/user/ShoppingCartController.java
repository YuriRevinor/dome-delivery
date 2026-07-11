package com.yurirvs.dome.controller.user;


import com.yurirvs.dome.context.BaseContext;
import com.yurirvs.dome.dto.ShoppingCartDTO;
import com.yurirvs.dome.entity.ShoppingCart;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车接口")
@Slf4j
@RestController
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation("添加至购物车")
    @PostMapping("/add")
    public Result<String> addToCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加至购物车: {}", shoppingCartDTO);

        shoppingCartService.addToCart(shoppingCartDTO);

        return Result.success();
    }

    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> listCart() {
        Long userId = BaseContext.getCurrentId();
        log.info("查看购物车: {}", userId);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(userId);

        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> cleanCart() {
        Long userId = BaseContext.getCurrentId();
        log.info("清空购物车: {}", userId);

        shoppingCartService.cleanCart(userId);

        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("减少购物车中商品")
    public Result<String> subFromCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("减少购物车中商品: {}", shoppingCartDTO);

        shoppingCartService.subFromCart(shoppingCartDTO);

        return Result.success();
    }
}
