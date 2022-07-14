package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GoodInfo {
    private Integer id;
    private Integer storeId;
    private String title;
    private String primaryImage;
    private Integer available;
    private Integer minSalePrice;
    private Integer minLinePrice;
    private Integer maxSalePrice;
    private Integer maxLinePrice;
    private Integer stockQuantity;
    private Integer soldQuantity;
    private Integer isPut;
    @TableField(exist = false)
    private Date addTime;
    @TableField(exist = false)
    private Date updateTime;
    @TableField(exist = false)
    private Integer deleteFlag;
    @TableField(exist = false)
    private Integer adFlag;
    @TableField(exist = false)
    private Integer gridFlag;
    @TableField(exist = false)
    private List<Tag> tags;
    @TableField(exist = false)
    private String storeName;
    @TableField(exist = false)
    private String storePhone;
}
