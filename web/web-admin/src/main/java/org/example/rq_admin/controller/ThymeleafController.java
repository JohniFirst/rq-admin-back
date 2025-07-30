package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "thymeleaf样例", description = "thymeleaf页面测试")
@Controller
@ResponseBody
public class ThymeleafController {

    @GetMapping("/thymeleaf")
    public String thymeleaf() {
        return "thymeleaf";
    }
}
