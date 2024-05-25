package org.dingdang.domain;

import lombok.Data;
import org.dingdang.common.annotations.AutoSnowflakeId;

import java.security.Timestamp;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/9 15:46 星期四
 * @Description:
 */
@Data
public class UserWordBook {

    private Long userId;
    private Long bookNo;
    private boolean lazyMode;
    private Timestamp createTime;
    private Timestamp updateTime;
}