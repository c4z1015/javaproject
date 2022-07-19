package org.czb.xingcan.db.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class ImageChart {
    private Integer id;
    private Integer storeId;
    private Integer goodId;
    private String name;
    private String info;
    private String src;
    private String url;
    private String displayLocation;
    private Date addTime;
    private Date updateTime;
    private Integer deleteFlag;
}
