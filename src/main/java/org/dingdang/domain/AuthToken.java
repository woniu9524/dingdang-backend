package org.dingdang.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/5 15:19 星期日
 * @Description:
 */
@Data
@AllArgsConstructor
public class AuthToken implements Serializable {
    private String accessToken;
    private int expireIn;
}
