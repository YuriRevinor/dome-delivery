package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.EmployeeDTO;
import com.yurirvs.dome.dto.EmployeeLoginDTO;
import com.yurirvs.dome.dto.EmployeePageQueryDTO;
import com.yurirvs.dome.entity.Employee;
import com.yurirvs.dome.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    boolean addEmployee(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void toggleStatus(Long id, Integer status);

    Employee getEmployeeById(Long id);

    boolean updateEmployee(EmployeeDTO employeeDTO);
}
