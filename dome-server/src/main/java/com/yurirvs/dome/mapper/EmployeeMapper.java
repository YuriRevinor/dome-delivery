package com.yurirvs.dome.mapper;

import com.github.pagehelper.Page;
import com.yurirvs.dome.dto.EmployeePageQueryDTO;
import com.yurirvs.dome.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("INSERT INTO employee VALUES " +
            "(null,#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status}," +
            "#{createTime},#{updateTime},#{createUser},#{updateUser})")
    int insert(Employee employee);


    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    int update(Employee employee);

    @Select("SELECT * FROM  employee WHERE id=#{id}")
    Employee getById(Long id);
}
