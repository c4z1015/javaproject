package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SubGoodInfo {
    private Integer id;
    private Integer parentId;
    private String title;
    private String primaryImage;
    @TableField(exist = false)
    private Date addTime;
    @TableField(exist = false)
    private Date updateTime;
    @TableField(exist = false)
    private Integer deleteFlag;
    @TableField(exist = false)
    private Integer storeId;
    @TableField(exist = false)
    private String storeName;
    @TableField(exist = false)
    private Integer buyQuantity;
    @TableField(exist = false)
    private Integer price;
    @TableField(exist = false)
    private Integer linePrice;
    @TableField(exist = false)
    private Integer isSelected;
    @TableField(exist = false)
    private List<GoodSpec> goodSpecs;
    @TableField(exist = false)
    private List<GoodPrice> goodPrices;
    @TableField(exist = false)
    private GoodStock goodStock;
}
