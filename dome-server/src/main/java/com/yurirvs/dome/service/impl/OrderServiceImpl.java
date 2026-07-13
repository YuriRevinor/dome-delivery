package com.yurirvs.dome.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.constant.StatusConstant;
import com.yurirvs.dome.context.BaseContext;
import com.yurirvs.dome.dto.*;
import com.yurirvs.dome.entity.*;
import com.yurirvs.dome.exception.AddressBookBusinessException;
import com.yurirvs.dome.exception.OrderBusinessException;
import com.yurirvs.dome.exception.ShoppingCartBusinessException;
import com.yurirvs.dome.mapper.*;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.service.OrderService;
import com.yurirvs.dome.utils.WeChatPayUtil;
import com.yurirvs.dome.vo.OrderPaymentVO;
import com.yurirvs.dome.vo.OrderStatisticsVO;
import com.yurirvs.dome.vo.OrderSubmitVO;
import com.yurirvs.dome.vo.OrderVO;
import com.yurirvs.dome.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

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

        StringBuilder sb = new StringBuilder();
        sb.append(addressBook.getProvinceName()).append(" ").append(addressBook.getCityName()).append(" ")
                .append(addressBook.getDistrictName()).append(" ").append(addressBook.getDetail());
        order.setAddress(sb.toString());

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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        /*//调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID;              // 支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;       // 订单状态，待接单

        // 发现没有将支付时间 check_out 属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        // 获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);

        Orders order = orderMapper.getByNumber(orderNumber);

        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", order.getId());
        map.put("content", "订单号: " + order.getNumber());

        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page page = orderMapper.getByUserIdWithDetails(BaseContext.getCurrentId());

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderVO getByIdWithDetails(Long id) {
        return orderMapper.getByIdWithDetails(id);
    }

    @Override
    public void cancelOrder(Long id) {
        Orders order = orderMapper.getById(id);

        // 校验订单是否存在
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 订单处于待接单状态下取消，需要进行退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {

            //支付状态修改为 退款
            order.setPayStatus(Orders.REFUND);
        }

        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);

    }

    @Override
    public void repeatOrder(Long id) {
        Long userId = BaseContext.getCurrentId();

        Orders order = orderMapper.getById(id);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());

        List<ShoppingCart> shoppingCarts = orderDetails.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Orders order = new Orders();
        BeanUtils.copyProperties(ordersPageQueryDTO, order);

        Page page = orderMapper.conditionSearch(ordersPageQueryDTO);

        List<OrderVO> result = (List<OrderVO>) page.getResult();

        for (OrderVO orderVO : result) {
            StringBuilder sb = new StringBuilder();
            for (OrderDetail orderDetail : orderVO.getOrderDetailList()) {
                sb.append(orderDetail.getName())
                        .append(" *").append(orderDetail.getNumber()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            orderVO.setOrderDishes(sb.toString());
        }


        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        orderStatisticsVO.setConfirmed(orderMapper.countByStatus(OrderVO.CONFIRMED));
        orderStatisticsVO.setToBeConfirmed(orderMapper.countByStatus(OrderVO.TO_BE_CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countByStatus(OrderVO.DELIVERY_IN_PROGRESS));

        return orderStatisticsVO;
    }

    @Override
    public void acceptOrder(Long id) {

        Orders order = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(order);
    }

    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getById(ordersRejectionDTO.getId());

        if (!order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        else {
            System.out.println(ordersRejectionDTO.getRejectionReason());
            Orders newOrder = Orders.builder()
                    .id(ordersRejectionDTO.getId())
                    .status(Orders.CANCELLED)
                    .payStatus(Orders.REFUND)
                    .rejectionReason(ordersRejectionDTO.getRejectionReason())
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.update(newOrder);
        }
    }

    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.getById(ordersCancelDTO.getId());

        if (order.getStatus() <= 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        else {
            Orders newOrder = Orders.builder()
                    .id(ordersCancelDTO.getId())
                    .status(Orders.CANCELLED)
                    .payStatus(Orders.REFUND)
                    .cancelReason(ordersCancelDTO.getCancelReason())
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.update(newOrder);
        }
    }

    @Override
    public void deliverOrder(Long id) {
        Orders order = orderMapper.getById(id);

        if (!order.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        else {
            Orders newOrder = Orders.builder()
                    .id(id)
                    .status(Orders.DELIVERY_IN_PROGRESS)
                    .build();
            orderMapper.update(newOrder);
        }
    }

    @Override
    public void completeOrder(Long id) {
        Orders order = orderMapper.getById(id);

        if (!order.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        else {
            Orders newOrder = Orders.builder()
                    .id(id)
                    .status(Orders.COMPLETED)
                    .deliveryTime(LocalDateTime.now())
                    .build();
            orderMapper.update(newOrder);
        }
    }

    @Override
    public void remindOrder(Long id) {
        Orders order = orderMapper.getById(id);

        if (!order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        else {

            Map map = new HashMap();
            map.put("type", 2);
            map.put("orderId", order.getId());
            map.put("content", "订单号: " + order.getNumber());

            String json = JSON.toJSONString(map);
            webSocketServer.sendToAllClient(json);

        }


    }


}
