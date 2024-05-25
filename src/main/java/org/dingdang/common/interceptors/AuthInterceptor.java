package org.dingdang.common.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dingdang.common.annotations.NoAuth;
import org.dingdang.common.utils.JwtUtil;
import org.dingdang.common.utils.UserContextUtil;
import org.dingdang.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 0:09 星期二
 * @Description: 鉴权拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 检查是否有 NoAuth 注解
            NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
            if (noAuth != null) {
                // 如果存在 NoAuth 注解，则无需鉴权，直接通过
                return true;
            }
            // 执行鉴权逻辑
            return checkAuthentication(request, response);
        }
        // 如果不是通过HandlerMethod映射的调用，如静态资源等，直接放行
        return true;

    }

    private boolean checkAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return validateToken(token);
        }
        // 无效的或缺失的认证信息
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置401状态码
        return false;
    }

    private boolean validateToken(String token) {
        try {
            SysUser sysUser = jwtUtil.parseToken(token);
            if (sysUser != null) {
                UserContextUtil.setCurrentUser(sysUser); // 将用户信息存储在ThreadLocal
                return true;
            }
        } catch (Exception e) {
            // Token解析失败，可能是因为过期或格式错误
            System.out.println(e);
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextUtil.clear(); // 确保在请求结束后清除ThreadLocal中的数据
    }
}
