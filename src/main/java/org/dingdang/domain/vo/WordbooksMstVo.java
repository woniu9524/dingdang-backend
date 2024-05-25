package org.dingdang.domain.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/10 4:19 星期五
 * @Description:
 */
@Data
public class WordbooksMstVo {

    private Long bookNo;

    private String title;

    private String description;

    private String tag;

    private String language;

    private String createBy;

    private Timestamp createTime;
    private String updateBy;

    private Timestamp updateTime;

    private String remark;

    private String status;

    private Long bookSeq;

    private Long wordCount;

    private boolean active;

    private Long masteredCount;

    private boolean lazyMode;

}
