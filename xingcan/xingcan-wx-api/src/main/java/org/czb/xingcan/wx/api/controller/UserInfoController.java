package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.UserInfo;
import org.czb.xingcan.db.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/info")
    public Object query(@RequestHeader("X-WX-OPENID") String openId) {
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq("open_id",openId);
        UserInfo userInfo = userInfoService.getOne(qw);
        if(userInfo!=null){
            userInfo.setOpenId("");
        }
        return ResponseUtil.ok(userInfo);
    }

    @PostMapping("/update")
    public Object update(@RequestHeader("X-WX-OPENID") String openId,@RequestBody UserInfo userInfo) {
        if(userInfo.getId()==null){
            userInfo.setOpenId(openId);
            userInfo.setMemberNumber("562344561");
            userInfo.setAddTime(new Date());
        }
        userInfo.setUpdateTime(new Date());
        userInfoService.saveOrUpdate(userInfo);
        return ResponseUtil.ok();
    }
}
