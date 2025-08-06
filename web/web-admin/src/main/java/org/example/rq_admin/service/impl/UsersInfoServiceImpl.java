package org.example.rq_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.entity.UserInfo;
import org.example.rq_admin.mapper.UsersInfoMapper;
import org.example.rq_admin.service.JwtService;
import org.example.rq_admin.service.UsersInfoService;
import org.springframework.stereotype.Service;

@Service
public class UsersInfoServiceImpl extends ServiceImpl<UsersInfoMapper, UserInfo> implements UsersInfoService {

    public UsersInfoServiceImpl(UsersInfoMapper usersInfoMapper, JwtService jwtService) {
        this.usersInfoMapper = usersInfoMapper;
        this.jwtService = jwtService;
    }

    private final UsersInfoMapper usersInfoMapper;
    private final JwtService jwtService;

    /**
     * @param userLoginDTO 用户登录的用户名密码
     * @return 数据库查询回来的用户名密码
     */
    @Override
    public String checkLoginInfo(UserLoginDTO userLoginDTO) {
        String token = "";
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userInfoLambdaQueryWrapper.eq(UserInfo::getUsername, userLoginDTO.getUsername()).eq(UserInfo::getPassword, userLoginDTO.getPassword());

        if (usersInfoMapper.selectCount(userInfoLambdaQueryWrapper) > 0) {
            token = jwtService.generateToken(userLoginDTO.getUsername());
        }

        return token;

    }

    /**
     * @param username 用户名
     * @return 是否存在用户名
     */
    @Override
    public Boolean checkUserExists(String username) {

        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userInfoLambdaQueryWrapper.eq(UserInfo::getUsername, username);

        Long l = usersInfoMapper.selectCount(userInfoLambdaQueryWrapper);

        return l > 0;
    }

}
