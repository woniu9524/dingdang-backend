package org.dingdang.domain;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/9 1:58 星期四
 * @Description:
 */
import lombok.Data;

import java.sql.Timestamp;

@Data
public class WordBooksMst {

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

}
