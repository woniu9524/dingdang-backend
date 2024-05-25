package org.dingdang.service;

import org.dingdang.common.ApiResponse;
import org.dingdang.domain.SysUser;
import org.springframework.http.ResponseEntity;


/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:50 星期二
 * @Description:
 */

public interface AuthService {


    ResponseEntity<byte[]> loginByWechat(String scene);


    ApiResponse isLogin(String scene);

    ApiResponse handleWechatCallback(String scene, String code,Boolean useScene);
}
