package com.yurirvs.dome.controller.admin;


import com.yurirvs.dome.constant.ShopConstant;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/shop")
@Api(tags = "店铺管理接口")
@Slf4j
@RestController("adminShopController")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @ApiOperation("设置店铺开张/打烊")
    @PutMapping("/{status}")
    public Result<String> toggleShopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态：{}", status.equals(ShopConstant.SHOP_STATUS_OPEN) ? "营业" : "打烊");

        shopService.toggleShopStatus(status);

        return Result.success();
    }

    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer status = shopService.getShopStatus();

        log.info("获取店铺营业状态: {}", status.equals(ShopConstant.SHOP_STATUS_OPEN) ? "营业" : "打烊");

        return Result.success(status);
    }
}
