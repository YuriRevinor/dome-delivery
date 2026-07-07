package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.EmployeeLoginDTO;
import com.yurirvs.dome.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
