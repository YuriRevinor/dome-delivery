package com.yurirvs.dome.mapper;

import com.yurirvs.dome.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void addBatch(List<OrderDetail> orderDetails);
}
