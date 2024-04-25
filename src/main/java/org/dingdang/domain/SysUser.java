package org.dingdang.domain;

import lombok.Data;
import org.dingdang.common.annotations.AutoSnowflakeId;

import java.io.Serializable;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:43 星期二
 * @Description:
 */
@Data
public class SysUser implements Serializable {

    @AutoSnowflakeId
    private Long userId;

    private String openId;

    private String username;

    private String password;
}