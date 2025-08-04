package org.example.rq_admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// 只在 default 环境生效
@Configuration
@Profile("default")
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 开发环境：所有请求直接放行
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // 关闭所有安全限制
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
