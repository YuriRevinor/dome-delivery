package com.yurirvs.dome.service.impl;

import com.yurirvs.dome.constant.ShopConstant;
import com.yurirvs.dome.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void toggleShopStatus(Integer status) {

        redisTemplate.opsForValue().set(ShopConstant.SHOP_STATUS,status);

    }

    @Override
    public Integer getShopStatus() {

        return (Integer) redisTemplate.opsForValue().get(ShopConstant.SHOP_STATUS);
    }
}
