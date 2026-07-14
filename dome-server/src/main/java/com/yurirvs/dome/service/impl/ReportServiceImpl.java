package com.yurirvs.dome.service.impl;

import com.yurirvs.dome.dto.GoodsSalesDTO;
import com.yurirvs.dome.entity.Orders;
import com.yurirvs.dome.mapper.OrderMapper;
import com.yurirvs.dome.mapper.UserMapper;
import com.yurirvs.dome.service.ReportService;
import com.yurirvs.dome.vo.OrderReportVO;
import com.yurirvs.dome.vo.SalesTop10ReportVO;
import com.yurirvs.dome.vo.TurnoverReportVO;
import com.yurirvs.dome.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new LinkedList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<BigDecimal> turnoverList = new LinkedList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<String, Object> map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            BigDecimal turnover = orderMapper.getAmountByDateAndStatus(map);

            if (turnover == null) {
                turnover = BigDecimal.ZERO;
            }

            turnoverList.add(turnover);
        }


        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new LinkedList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new LinkedList<>();
        List<Integer> totalUserList = new LinkedList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer newUser = userMapper.getByCreateTime(beginTime, endTime);
            Integer totalUser = userMapper.getByCreateTime(null, endTime);

            if (newUser == null) {
                newUser = 0;
            }
            if (totalUser == null) {
                totalUser = 0;
            }
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new LinkedList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new LinkedList<>();
        List<Integer> validOrderCountList = new LinkedList<>();

        Integer totalOrderCount = orderMapper.getCountByOrderTimeAndStatus(null, null, null);
        Integer validOrderCount = orderMapper.getCountByOrderTimeAndStatus(null, null, Orders.COMPLETED);

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCountDay = orderMapper.getCountByOrderTimeAndStatus(beginTime, endTime, null);
            Integer validOrderCountDay = orderMapper.getCountByOrderTimeAndStatus(beginTime, endTime, Orders.COMPLETED);

            if (orderCountDay == null) {
                orderCountDay = 0;
            }
            if (validOrderCountDay == null) {
                validOrderCountDay = 0;
            }
            orderCountList.add(orderCountDay);
            validOrderCountList.add(validOrderCountDay);
        }

        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList))
                .validOrderCountList(StringUtils.join(validOrderCountList))
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSales = orderMapper.getSalesTop10(beginTime, endTime);

        StringBuilder nameList = new StringBuilder();
        StringBuilder numberList = new StringBuilder();


        for (GoodsSalesDTO sale : goodsSales) {
            nameList.append(sale.getName());
            nameList.append(",");

            numberList.append(sale.getNumber());
            numberList.append(",");
        }
        nameList.deleteCharAt(nameList.lastIndexOf(","));
        numberList.deleteCharAt(numberList.lastIndexOf(","));

        return SalesTop10ReportVO
                .builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
    }
}
