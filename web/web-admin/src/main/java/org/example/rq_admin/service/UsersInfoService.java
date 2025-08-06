package org.example.rq_admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.entity.UserInfo;

public interface UsersInfoService extends IService<UserInfo> {

    String checkLoginInfo(UserLoginDTO userLoginDTO);

    Boolean checkUserExists(String username);
}
