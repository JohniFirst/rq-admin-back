package org.example.rq_admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 替代旧版 @EnableGlobalMethodSecurity，支持 @PreAuthorize 等注解
@Profile({"prod", "test"})
public class SecurityConfig {

    // JWT 过滤器（需自行实现）
    private final JwtAuthenticationFilter jwtAuthFilter;
    // 用户详情服务（需自行实现，用于加载用户信息）
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 认证管理器（Spring Security 6 中需手动配置）
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    // 核心安全过滤链配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（前后端分离场景常用）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理为无状态（JWT 认证模式）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置 URL 访问权限
                .authorizeHttpRequests(auth -> auth
                        // 放行用户获取认证相关接口，约定以auth开头
                        .requestMatchers("/auth/**").permitAll()
                        // 放行静态资源和 Swagger 文档（开发环境用）
                        .requestMatchers("/doc.html", "/webjars/**","/v3/**", "/favicon.ico","/static/**").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                // 配置跨域
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 添加 JWT 过滤器（在用户名密码过滤器之前执行）
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 跨域配置
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        // 允许的前端域名（生产环境需指定具体域名，不要用 *）
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:8080"));
//        // 允许的请求方法
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        // 允许的请求头（包含自定义的 Token 头）
//        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
//        // 允许前端获取的响应头
//        config.setExposedHeaders(List.of("Authorization"));
//        // 允许携带 Cookie（如需）
//        config.setAllowCredentials(true);
//        // 预检请求缓存时间（减少请求次数）
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}