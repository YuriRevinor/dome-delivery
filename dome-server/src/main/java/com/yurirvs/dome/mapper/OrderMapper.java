package com.yurirvs.dome.mapper;

import com.yurirvs.dome.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {


    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("UPDATE orders " +
            "SET status = #{orderStatus}, " +
            "    pay_status = #{orderPaidStatus}, " +
            "    checkout_time = #{checkOutTime} " +
            "WHERE number = #{orderNumber}")
    void updateStatus(@Param("orderStatus") Integer orderStatus,
                      @Param("orderPaidStatus") Integer orderPaidStatus,
                      @Param("checkOutTime") LocalDateTime checkOutTime,
                      @Param("orderNumber") String orderNumber);
}
