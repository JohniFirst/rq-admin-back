package org.example.rq_admin.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.entity.ThymeleafFormTest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "参数接收测试", description = "列出spring常用的参数接受方式")
@RestController
@RequestMapping("/param")
public class ParamsResolveController {

    @ApiOperationSupport(author = "zhangsan")
    @Operation(summary = "get接口", description = "常见的get传参，将接收到的参数以字符串的方式返回")
    @GetMapping("/get")
    public String get(String name1, Integer age1) {
        return "name1=  "+ name1 + "age1=    " + age1.toString();
    }

    @Operation(summary = "PathVariable方式接收参数", description = "rest风格路径参数")
    @RequestMapping(value = "/pathVariable/{name}", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE
    })
    public String getPathVariable(@PathVariable String name) {
        return name;
    }

    @Operation(summary = "接收json参数", description = "接收请求体里面的参数，application/json格式的参数")
    @RequestMapping(value = "/post", method = {
            RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE
    })
    public String post(@RequestBody ThymeleafFormTest thymeleafFormTest) {
        return thymeleafFormTest.toString();
    }

    @Operation(summary = "接收form表单参数", description = "接收常规的表单方式提交的参数，application/x-www-form-urlencoded")
    @RequestMapping(value = "/post/form", method = {
            RequestMethod.POST, RequestMethod.PUT
    })
    public String postFormData(ThymeleafFormTest thymeleafFormTest) {
        return thymeleafFormTest.toString();
    }

    @Operation(summary = "解析请求头", description = "获取请求头里面的参数")
    @GetMapping("/header")
    public String getHeader(@RequestHeader("Content-Type") String contentType) {
        return contentType;
    }

    @Operation(summary = "解析cookie", description = "获取cookie里面的值")
    @GetMapping("/cookie")
    public String getCookie(@CookieValue("token") String token, String name) {
        return token + "    name=" + name;
    }
}
