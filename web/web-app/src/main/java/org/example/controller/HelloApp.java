package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.common.R;
import org.example.entity.UserInfo;
import org.example.exception.BizException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class HelloApp {

    @GetMapping("/params")
    public String handleParams(@RequestParam("username") List<Integer> username) {
        System.out.println(username);
        for (Integer number : username) {
            System.out.println(number);
        }
        return username.toString();
    }

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
