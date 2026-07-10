package com.yurirvs.dome.service;

import com.yurirvs.dome.dto.UserLoginDTO;
import com.yurirvs.dome.entity.User;
import com.yurirvs.dome.vo.UserLoginVO;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);
}
