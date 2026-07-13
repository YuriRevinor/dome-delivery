package com.yurirvs.dome.controller.user;

import com.yurirvs.dome.dto.OrdersPageQueryDTO;
import com.yurirvs.dome.dto.OrdersPaymentDTO;
import com.yurirvs.dome.dto.OrdersSubmitDTO;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.OrderService;
import com.yurirvs.dome.vo.OrderPaymentVO;
import com.yurirvs.dome.vo.OrderSubmitVO;
import com.yurirvs.dome.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "用户订单接口")
@RequestMapping("/user/order")
@RestController("userOrderController")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("用户提交订单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> orderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单: {}", ordersSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.orderSubmit(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @ApiOperation("分页查询订单")
    @GetMapping("/historyOrders")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询订单: {}", ordersPageQueryDTO);

        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getByIdWithDetails(@PathVariable Long id) {
        log.info("获得订单详情: {}", id);

        OrderVO orderVO = orderService.getByIdWithDetails(id);

        return Result.success(orderVO);
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result<String> cancelOrder(@PathVariable Long id) {
        log.info("取消订单: {}", id);

        orderService.cancelOrder(id);

        return Result.success();
    }

    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result<String> repeatOrder(@PathVariable Long id) {
        log.info("再来一单: {}", id);

        orderService.repeatOrder(id);

        return Result.success();
    }

    @ApiOperation("客户催单")
    @GetMapping("/reminder/{id}")
    public Result<String> remindOrder(@PathVariable Long id) {
        log.info("客户催单: {}", id);

        orderService.remindOrder(id);

        return Result.success();
    }
}
