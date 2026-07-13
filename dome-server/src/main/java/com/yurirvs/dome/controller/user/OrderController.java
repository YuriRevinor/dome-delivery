package com.yurirvs.dome.controller.user;

import com.yurirvs.dome.dto.OrdersPaymentDTO;
import com.yurirvs.dome.dto.OrdersSubmitDTO;
import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.OrderService;
import com.yurirvs.dome.vo.OrderPaymentVO;
import com.yurirvs.dome.vo.OrderSubmitVO;
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
        log.info("用户提交订单: {}",ordersSubmitDTO);

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
}
