package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class WebAppMain {
    public static void main(String[] args) {
        SpringApplication.run(WebAppMain.class, args);
    }
}