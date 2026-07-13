package com.yurirvs.dome.mapper;

import com.github.pagehelper.Page;
import com.yurirvs.dome.dto.OrdersPageQueryDTO;
import com.yurirvs.dome.entity.Orders;
import com.yurirvs.dome.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDateTime;
import java.util.List;

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


    Page getByUserIdWithDetails(Long UserId);

    OrderVO getByIdWithDetails(Long id);

    @Select("SELECT * FROM orders WHERE id=#{id}")
    Orders getById(Long id);

    Page conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("SELECT COUNT(*) FROM orders WHERE status=#{status}")
    Integer countByStatus(Integer status);

    @Select("SELECT * FROM orders WHERE status=#{status} AND order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT (Integer status, LocalDateTime orderTime);
}
