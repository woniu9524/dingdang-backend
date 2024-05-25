package org.dingdang.domain.vo;

import lombok.Data;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/11 3:58 星期六
 * @Description:
 */
@Data
public class WordbookWord {
    private String word;
    private String translation;
    private String sentence;
    private String sentenceTranslation;
    private String lazyTranslation;
    private String phonetic;
    private Integer lastGrade;
    private Integer eFactor;
}