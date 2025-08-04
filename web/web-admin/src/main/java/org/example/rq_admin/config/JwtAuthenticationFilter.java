package org.example.rq_admin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.rq_admin.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器，用于验证请求中的JWT令牌并设置认证信息
 * 继承OncePerRequestFilter确保每次请求只执行一次过滤逻辑
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 从请求头中获取Authorization字段
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 检查Authorization头是否存在且格式正确（Bearer + 令牌）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 不满足条件则直接放行，进入下一个过滤器
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT令牌（去除"Bearer "前缀）
        jwt = authHeader.substring(7);

        try {
            // 从JWT令牌中提取用户名
            username = jwtService.extractUsername(jwt);

            // 检查用户名是否存在且当前上下文未认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 验证令牌是否有效
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    // 设置请求详情
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将认证信息设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 记录令牌验证过程中的异常，不中断过滤器链
            logger.error("JWT令牌验证失败: {}", e);
        }

        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}
    