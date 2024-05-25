package org.dingdang.domain;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/25 11:02 星期六
 * @Description:
 */
import lombok.Data;

import java.util.Date;

@Data
public class DailyLearningStats {

    private Long userId;
    private Date learningDate;
    private int newWordsCount;
    private int reviewedWordsCount;

}