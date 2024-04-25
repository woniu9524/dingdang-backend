package org.dingdang.service.impl;

import lombok.AllArgsConstructor;
import org.dingdang.common.Constants;
import org.dingdang.domain.SysUser;
import org.dingdang.mapper.AuthMapper;
import org.dingdang.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.dingdang.common.utils.JwtUtil;
/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:51 星期二
 * @Description:
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;

    private final JwtUtil jwtUtil;

    private final RedisTemplate redisTemplate;

    @Override
    public String register(SysUser sysUser) {
        String token = jwtUtil.generateToken(sysUser.getUserId(), sysUser.getOpenId());
        System.out.println("token: " + token);
        return authMapper.register(sysUser) > 0 ? "注册成功" : "注册失败";
    }
}