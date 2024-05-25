package org.dingdang.domain;

import lombok.Data;
import org.dingdang.common.annotations.AutoSnowflakeId;

import java.sql.Timestamp;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/23 4:54 星期四
 * @Description:
 */
@Data
public class WordGenerateHistory {
    @AutoSnowflakeId
    private Long generateId;
    private Long userId;
    private Timestamp timestamp;
    private String generateText;
}