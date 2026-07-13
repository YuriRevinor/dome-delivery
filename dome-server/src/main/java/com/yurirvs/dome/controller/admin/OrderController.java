package com.yurirvs.dome.controller.admin;


import com.yurirvs.dome.dto.OrdersCancelDTO;
import com.yurirvs.dome.dto.OrdersConfirmDTO;
import com.yurirvs.dome.dto.OrdersPageQueryDTO;
import com.yurirvs.dome.dto.OrdersRejectionDTO;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.OrderService;
import com.yurirvs.dome.vo.OrderStatisticsVO;
import com.yurirvs.dome.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "管理端订单接口")
@RequestMapping("/admin/order")
@RestController("adminOrderController")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("条件查询订单")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("条件分页查询订单: {}", ordersPageQueryDTO);

        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    @ApiOperation("订单状态统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("订单状态统计");

        OrderStatisticsVO orderStatisticsVO = orderService.getStatistics();

        return Result.success(orderStatisticsVO);
    }

    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result<String> acceptOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单: {}", ordersConfirmDTO);

        orderService.acceptOrder(ordersConfirmDTO.getId());

        return Result.success();
    }

    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result<String> rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单: {}", ordersRejectionDTO);

        orderService.rejectOrder(ordersRejectionDTO);

        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result<String> cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单: {}", ordersCancelDTO);

        orderService.adminCancelOrder(ordersCancelDTO);

        return Result.success();
    }

    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result<String> deliverOrder(@PathVariable Long id) {
        log.info("派送订单: {}", id);

        orderService.deliverOrder(id);

        return Result.success();
    }

    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result<String> completeOrder(@PathVariable Long id) {
        log.info("完成订单: {}", id);

        orderService.completeOrder(id);

        return Result.success();
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("获取订单详情: {}",id);

        OrderVO orderVO = orderService.getByIdWithDetails(id);

        return Result.success(orderVO);
    }
}
