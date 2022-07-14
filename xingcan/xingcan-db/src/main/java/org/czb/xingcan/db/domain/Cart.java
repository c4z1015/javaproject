package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class Cart {
    private Integer id;
    private String openId;
    private Integer storeId;
    private Integer subGoodId;
    private Integer buyQuantity;
    private Integer isSelected;
    private Date addTime;
    private Date updateTime;
    @TableField(exist = false)
    private Integer deleteFlag;
}
