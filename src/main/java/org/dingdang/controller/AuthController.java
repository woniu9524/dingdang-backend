package org.dingdang.controller;

import lombok.AllArgsConstructor;
import org.dingdang.common.ApiResponse;
import org.dingdang.common.Constants;
import org.dingdang.common.annotations.NoAuth;
import org.dingdang.common.utils.CaffeineCacheUtil;
import org.dingdang.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.dingdang.service.AuthService;
/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:45 星期二
 * @Description:
 */
@RestController
@RequestMapping("v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 登录
    @NoAuth
    @GetMapping("/login/wechat")
    public ResponseEntity<byte[]> loginByWechat(@RequestParam("scene") String scene) {
        return authService.loginByWechat(scene);
    }

    @NoAuth
    @GetMapping("/wechat/callback")
    public ApiResponse handleWechatCallback(@RequestParam("scene") String scene, @RequestParam("code") String code,@RequestParam("useScene") Boolean useScene) {
        return authService.handleWechatCallback(scene, code,useScene);

    }

    @NoAuth
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    // 前端调用该接口是否登录
    @NoAuth
    @GetMapping("/isLogin/{scene}")
    public ApiResponse isLogin(@PathVariable("scene") String scene){
        return authService.isLogin(scene);
    }
}