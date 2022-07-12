package org.czb.xingcan.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Store {
    private Integer id;
    private String title;
    private String telephone;
    private String primaryImage;
    private String address;
    private String longitude;
    private String latitude;
    private String mainBusiness;
    private String notice;
    @TableField(exist = false)
    private String info;
    @TableField(exist = false)
    private String belong;
    @TableField(exist = false)
    private Date addTime;
    @TableField(exist = false)
    private Date updateTime;
    @TableField(exist = false)
    private Integer deleteFlag;
    @TableField(exist = false)
    private String remark;
    @TableField(exist = false)
    private List<ImageChart> imgs;
    @TableField(exist = false)
    private List<SubGoodInfo> subGoodInfos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getMainBusiness() {
        return mainBusiness;
    }

    public void setMainBusiness(String mainBusiness) {
        this.mainBusiness = mainBusiness;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<ImageChart> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImageChart> imgs) {
        this.imgs = imgs;
    }

    public List<SubGoodInfo> getSubGoodInfos() {
        return subGoodInfos;
    }

    public void setSubGoodInfos(List<SubGoodInfo> subGoodInfos) {
        this.subGoodInfos = subGoodInfos;
    }
}
