package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.OrdersSubmitDTO;
import com.yurirvs.dome.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);
}
