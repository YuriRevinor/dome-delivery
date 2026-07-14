package com.yurirvs.dome.controller.admin;


import com.yurirvs.dome.result.Result;
import com.yurirvs.dome.service.ReportService;
import com.yurirvs.dome.vo.OrderReportVO;
import com.yurirvs.dome.vo.SalesTop10ReportVO;
import com.yurirvs.dome.vo.TurnoverReportVO;
import com.yurirvs.dome.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@Api(tags = "数据统计接口")
@Slf4j
@RequestMapping("/admin/report")
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;


    @ApiOperation("营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("营业额统计: {} -> {}", begin, end);

        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);

        return Result.success(turnoverReportVO);
    }

    @ApiOperation("用户数统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户数统计: {} -> {}", begin, end);

        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);

        return Result.success(userReportVO);
    }

    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrdersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计: {} -> {}", begin, end);

        OrderReportVO orderReportVO = reportService.getOrdersStatistics(begin, end);

        return Result.success(orderReportVO);
    }

    @ApiOperation("销量排名前十菜品")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名前十菜品: {} -> {}", begin, end);

        SalesTop10ReportVO salesTop10ReportVO = reportService.getTop10(begin, end);

        return Result.success(salesTop10ReportVO);
    }
}
