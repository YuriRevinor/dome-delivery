package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.*;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.vo.OrderPaymentVO;
import com.yurirvs.dome.vo.OrderStatisticsVO;
import com.yurirvs.dome.vo.OrderSubmitVO;
import com.yurirvs.dome.vo.OrderVO;

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

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getByIdWithDetails(Long id);

    void cancelOrder(Long id);

    void repeatOrder(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getStatistics();

    void acceptOrder(Long id);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void deliverOrder(Long id);

    void completeOrder(Long id);

    void remindOrder(Long id);
}
