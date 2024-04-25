package org.dingdang.common.utils;

import io.jsonwebtoken.Claims;
import org.dingdang.domain.SysUser;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 3:26 星期二
 * @Description:
 */
public class UserContextUtil {
    private static final ThreadLocal<SysUser> userClaims = new ThreadLocal<>();

    public static void setCurrentUser(SysUser claims) {
        userClaims.set(claims);
    }

    public static SysUser getCurrentUser() {
        return userClaims.get();
    }

    public static void clear() {
        userClaims.remove();
    }
}