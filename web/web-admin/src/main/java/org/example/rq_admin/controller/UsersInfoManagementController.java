package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.entity.UserInfo;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.impl.UsersInfoServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户信息管理", description = "出去注册、登录之外的用户信息管理逻辑")
@RestController
@RequestMapping("/userInfo")
public class UsersInfoManagementController {

    UsersInfoServiceImpl usersInfoService;

    public UsersInfoManagementController(UsersInfoServiceImpl usersInfoService) {
        this.usersInfoService = usersInfoService;
    }

    @Operation(summary = "用户列表")
    @GetMapping("/userList")
    public FormatResponseData<List<UserInfo>> userList() {
        List<UserInfo> list = usersInfoService.list();

        return FormatResponseData.ok(list);
    }

    @Operation(summary = "用户信息查询")
    @GetMapping
    public FormatResponseData<UserInfo> getUserInfoById(@RequestParam Long id) {
        UserInfo userInfo = usersInfoService.getById(id);
        return FormatResponseData.ok(userInfo);
    }
}
