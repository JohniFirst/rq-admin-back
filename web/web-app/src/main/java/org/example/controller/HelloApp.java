package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.common.R;
import org.example.entity.UserInfo;
import org.example.exception.BizException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class HelloApp {

    @Operation(summary = "hello", description = "你好，世界")
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public R<String> hello(String name) {
        if (name.length() < 3) {
            throw new BizException("name的长度必须大于2");
        }

        return R.success("Hello World" + name);
    }

    @GetMapping("/validate")
    public R<UserInfo> validate(@Valid UserInfo userInfo) {
        return R.success(userInfo);
    }
}
