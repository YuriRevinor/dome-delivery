package com.yurirvs.dome.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.constant.PasswordConstant;
import com.yurirvs.dome.constant.StatusConstant;
import com.yurirvs.dome.context.BaseContext;
import com.yurirvs.dome.dto.EmployeeDTO;
import com.yurirvs.dome.dto.EmployeeLoginDTO;
import com.yurirvs.dome.dto.EmployeePageQueryDTO;
import com.yurirvs.dome.entity.Employee;
import com.yurirvs.dome.exception.AccountLockedException;
import com.yurirvs.dome.exception.AccountNotFoundException;
import com.yurirvs.dome.exception.PasswordErrorException;
import com.yurirvs.dome.mapper.EmployeeMapper;
import com.yurirvs.dome.result.PageResult;
import com.yurirvs.dome.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //MD5 encryption
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public boolean addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //Copy properties
        BeanUtils.copyProperties(employeeDTO, employee);

        //Enable employee account
        employee.setStatus(StatusConstant.ENABLE);

        //Default password
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //Set time
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //Set Creator ID
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        if (employeeMapper.insert(employee) == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void toggleStatus(Long id, Integer status) {

        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);

        employeeMapper.update(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {

        Employee employee = employeeMapper.getById(id);
        employee.setPassword("******");

        return employee;
    }

    @Override
    public boolean updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setUpdateTime(LocalDateTime.now());

        if (employeeMapper.update(employee) == 1) {
            return true;
        }
        else {
            return false;
        }
    }
}
