package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.Cart;
import org.czb.xingcan.db.domain.Order;
import org.czb.xingcan.db.domain.UserAddress;
import org.czb.xingcan.db.domain.UserInfo;
import org.czb.xingcan.db.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping("/user")
public class WxUserController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/info")
    public Object info(@RequestHeader("X-WX-OPENID") String openId) {
        List<Map<String,Long>> maps = new ArrayList<>();
        QueryWrapper<Order> qw = new QueryWrapper<>();
        qw.eq("open_id",openId);
        qw.eq("delete_flag",0);
        qw.eq("order_status",5);
        Long count = userInfoService.queryUserOrderCount(qw);
        Map<String,Long> map = new HashMap<>();
        map.put("orderNum",count);
        maps.add(map);
        Map<String,Long> mapNon = new HashMap<>();
        mapNon.put("orderNum",0l);
        maps.add(mapNon);
        maps.add(mapNon);
        maps.add(mapNon);
        return ResponseUtil.ok(maps);
    }

    @GetMapping("/address")
    public Object address(@RequestHeader("X-WX-OPENID") String openId) {
        QueryWrapper<UserAddress> qw = new QueryWrapper<>();
        qw.eq("open_id",openId);
        qw.orderByAsc("is_default");
        List<UserAddress> userAddressList = userInfoService.queryUserAddressList(qw);
        return ResponseUtil.ok(userAddressList);
    }

    @PostMapping("/update")
    public Object update(@RequestHeader("X-WX-OPENID") String openId,@RequestBody UserInfo userInfo) {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<UserInfo> userCallable = ()->{
            QueryWrapper<UserInfo> qw = new QueryWrapper<>();
            qw.eq("open_id",openId);
            UserInfo user = userInfoService.getOne(qw);
            if(user != null){
                UpdateWrapper<UserInfo> uw = new UpdateWrapper<>();
                user.setUpdateTime(new Date());
                uw.eq("open_id",openId);
                userInfoService.update(user,uw);
                return user;
            }else{
                userInfo.setOpenId(openId);
                userInfo.setMemberNumber("562344561");
                userInfo.setAddTime(new Date());
                userInfoService.save(userInfo);
                return userInfo;
            }
        };
        Callable<List<Map<String,Long>>> countCallable = ()->{
            List<Map<String,Long>> maps = new ArrayList<>();
            QueryWrapper<Order> qw = new QueryWrapper<>();
            qw.eq("open_id",openId);
            qw.eq("delete_flag",0);
            qw.eq("order_status",5);
            Long count = userInfoService.queryUserOrderCount(qw);
            Map<String,Long> map = new HashMap<>();
            map.put("orderNum",count);
            maps.add(map);
            Map<String,Long> mapNon = new HashMap<>();
            mapNon.put("orderNum",0l);
            maps.add(mapNon);
            maps.add(mapNon);
            maps.add(mapNon);
            return maps;
        };

        FutureTask<UserInfo> userTask = new FutureTask<>(userCallable);
        FutureTask<List<Map<String,Long>>> countTask = new FutureTask<>(countCallable);

        executorService.submit(userTask);
        executorService.submit(countTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("userInfo", userTask.get());
            entity.put("orderTagInfos", countTask.get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            //重要！关闭创建的线程池
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }

    @PostMapping("/updateAddress")
    public Object updateAddress(@RequestHeader("X-WX-OPENID") String openId,@RequestBody UserAddress userAddress) {
        userInfoService.saveOrUpdateAddress(openId , userAddress);
        return ResponseUtil.ok();
    }

    @PostMapping("/deleteAddress")
    public Object deleteAddress(@RequestHeader("X-WX-OPENID") String openId,@RequestBody UserAddress userAddress) {
        userInfoService.deleteAddress(userAddress);
        return ResponseUtil.ok();
    }

    @PostMapping("/telephone")
    public Object telephone(@RequestHeader("X-WX-OPENID") String openId,@RequestBody UserInfo userInfo) {
        UpdateWrapper<UserInfo> uw = new UpdateWrapper<>();
        uw.set("update_time",new Date());
        uw.set("telephone",userInfo.getTelephone());
        uw.eq("open_id",openId);
        userInfoService.update(null,uw);
        return ResponseUtil.ok();
    }
}
