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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
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

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getMinSalePrice() {
        return minSalePrice;
    }

    public void setMinSalePrice(Integer minSalePrice) {
        this.minSalePrice = minSalePrice;
    }

    public Integer getMinLinePrice() {
        return minLinePrice;
    }

    public void setMinLinePrice(Integer minLinePrice) {
        this.minLinePrice = minLinePrice;
    }

    public Integer getMaxSalePrice() {
        return maxSalePrice;
    }

    public void setMaxSalePrice(Integer maxSalePrice) {
        this.maxSalePrice = maxSalePrice;
    }

    public Integer getMaxLinePrice() {
        return maxLinePrice;
    }

    public void setMaxLinePrice(Integer maxLinePrice) {
        this.maxLinePrice = maxLinePrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Integer getIsPut() {
        return isPut;
    }

    public void setIsPut(Integer isPut) {
        this.isPut = isPut;
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

    public Integer getAdFlag() {
        return adFlag;
    }

    public void setAdFlag(Integer adFlag) {
        this.adFlag = adFlag;
    }

    public Integer getGridFlag() {
        return gridFlag;
    }

    public void setGridFlag(Integer gridFlag) {
        this.gridFlag = gridFlag;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
