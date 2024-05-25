package org.dingdang.domain.vo;

import lombok.Data;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/25 11:04 星期六
 * @Description:
 */
@Data
public class WordMasteryVo {

    private Long bookNo;
    private int unfamiliar;
    private int difficult;
    private int good;
    private int easy;
    private int mastered;
    private int fullyMastered;
}