package org.czb.xingcan.db.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Cargo {
    private Integer id;
    private String title;
    private String content;
    private String image;
    private String info;
    private String address;
    private String phone;
    private String notice;
    private Integer cargoFlag;
    private Date addTime;
    private Date updateTime;
    private Integer deleteFlag;
}
