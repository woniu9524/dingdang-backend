package org.dingdang.controller;

import lombok.AllArgsConstructor;
import org.dingdang.common.annotations.NoAuth;
import org.dingdang.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.dingdang.service.AuthService;
/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:45 星期二
 * @Description:
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 注册
    @NoAuth
    @PostMapping("/register")
    public String register(SysUser sysUser) {
        return authService.register(sysUser);
    }

}