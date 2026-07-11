package com.yurirvs.dome.mapper;

import com.yurirvs.dome.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {


    void insert(Orders order);
}
