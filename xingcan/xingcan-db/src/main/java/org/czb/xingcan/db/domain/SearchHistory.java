package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class SearchHistory {
    private Integer id;
    private String openId;
    private String keyword;
    private Integer frequency;
    private String source;
    private Date addTime;
    private Date updateTime;
    private Integer deleteFlag;
}
