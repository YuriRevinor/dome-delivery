package com.yurirvs.dome.service.impl;

import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.context.BaseContext;
import com.yurirvs.dome.dto.OrdersSubmitDTO;
import com.yurirvs.dome.entity.AddressBook;
import com.yurirvs.dome.entity.OrderDetail;
import com.yurirvs.dome.entity.Orders;
import com.yurirvs.dome.entity.ShoppingCart;
import com.yurirvs.dome.exception.AddressBookBusinessException;
import com.yurirvs.dome.exception.ShoppingCartBusinessException;
import com.yurirvs.dome.mapper.AddressBookMapper;
import com.yurirvs.dome.mapper.OrderDetailMapper;
import com.yurirvs.dome.mapper.OrderMapper;
import com.yurirvs.dome.mapper.ShoppingCartMapper;
import com.yurirvs.dome.service.OrderService;
import com.yurirvs.dome.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //检查地址簿
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //获取购物车内容
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //订单表处理
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);

        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setUserId(BaseContext.getCurrentId());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(order);
        //订单明细表处理
        List<OrderDetail> orderDetails = new LinkedList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetail.setId(null);
            orderDetails.add(orderDetail);
        }

        orderDetailMapper.addBatch(orderDetails);
        //清空购物车
        shoppingCartMapper.DeleteByUserId(BaseContext.getCurrentId());
        //构造返回对象
        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
    }
}
