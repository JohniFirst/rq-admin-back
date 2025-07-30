package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.entity.ThymeleafFormTest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "thymeleaf样例", description = "thymeleaf页面测试")
@Controller
public class ThymeleafController {

    @GetMapping("/thymeleaf")
    public String thymeleaf() {
        return "thymeleaf";
    }

    @PostMapping("/thymeleaf/form")
    public String thymeleafForm(
            ThymeleafFormTest thymeleafFormTest,
            Model model
    ) {
        model.addAttribute("user", thymeleafFormTest);
        return "submit-success";
    }
}
