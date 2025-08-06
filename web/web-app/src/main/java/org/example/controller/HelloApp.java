package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class HelloApp {

    @Operation(summary = "hello", description = "你好，世界")
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> hello(@Validated @Size(min = 2) String name) {
        return ResponseEntity.ok("Hello World" + name);
    }
}
