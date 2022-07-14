package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private String openId;
    private String orderNo;
    private Integer orderStatus;
    private String orderStatusName;
    private Integer totalAmount;
    private Integer paymentAmount;
    private Integer discountAmount;
    private Integer freightFee;
    private String remark;
    private Integer storeId;
    private String storeName;
    private String storePhone;
    private String userName;
    private String userPhone;
    private String detailAddress;
    private String latitude;
    private String longitude;
    private Integer invoiceStatus;
    private String invoiceDesc;
    private Date addTime;
    private Date updateTime;
    @TableField(exist = false)
    private Integer deleteFlag;
    @TableField(exist = false)
    private String createTime;
    @TableField(exist = false)
    private List<OrderGood> goodsList;
    @TableField(exist = false)
    private List<OrderButton> buttons;
}
