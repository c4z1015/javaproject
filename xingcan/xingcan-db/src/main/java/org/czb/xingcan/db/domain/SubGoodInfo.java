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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(Integer buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getLinePrice() {
        return linePrice;
    }

    public void setLinePrice(Integer linePrice) {
        this.linePrice = linePrice;
    }

    public Integer getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Integer isSelected) {
        this.isSelected = isSelected;
    }

    public List<GoodSpec> getGoodSpecs() {
        return goodSpecs;
    }

    public void setGoodSpecs(List<GoodSpec> goodSpecs) {
        this.goodSpecs = goodSpecs;
    }

    public List<GoodPrice> getGoodPrices() {
        return goodPrices;
    }

    public void setGoodPrices(List<GoodPrice> goodPrices) {
        this.goodPrices = goodPrices;
    }

    public GoodStock getGoodStock() {
        return goodStock;
    }

    public void setGoodStock(GoodStock goodStock) {
        this.goodStock = goodStock;
    }
}
