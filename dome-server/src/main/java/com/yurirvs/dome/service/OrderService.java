package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.OrdersPaymentDTO;
import com.yurirvs.dome.dto.OrdersSubmitDTO;
import com.yurirvs.dome.vo.OrderPaymentVO;
import com.yurirvs.dome.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
}
