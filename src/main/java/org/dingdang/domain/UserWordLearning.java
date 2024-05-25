package org.dingdang.domain;

import lombok.Data;
import org.dingdang.common.annotations.AutoSnowflakeId;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/10 4:49 星期五
 * @Description:
 */

@Data
public class UserWordLearning {
    private Long userId;
    private Long bookNo;
    private String word;
    private int reviewCount;
    private Timestamp lastReviewDate;
    private Timestamp nextReviewDate;
    private String translation;
    private String lazyTranslation;
    private double eFactor;
    private int interval;
    private String sentence;
    private String sentenceTranslation;
    private String language;
    private int lastGrade;
    private int needReview;
}