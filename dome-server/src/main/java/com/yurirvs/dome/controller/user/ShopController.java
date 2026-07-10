package com.yurirvs.dome.controller.user;


import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/shop")
@Api(tags = "店铺管理接口")
@Slf4j
@RestController("userShopController")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer status = shopService.getShopStatus();

        log.info("获取店铺营业状态: {}", status == 1 ? "营业" : "打烊");

        return Result.success(status);
    }
}
