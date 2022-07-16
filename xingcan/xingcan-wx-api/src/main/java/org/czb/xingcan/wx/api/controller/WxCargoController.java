package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.CargoService;
import org.czb.xingcan.db.service.ImageChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping("/cargo")
public class WxCargoController {
    //默认尾货求购显示的数量
    private final static  Integer CARGO_NUMBER = 10;

    @Autowired
    private ImageChartService imageChartService;

    @Autowired
    private CargoService cargoService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("flag") long flag) {
        System.out.println(flag);
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //获取求购尾货轮播图广告位数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("display_location","supply-cargo-head-"+flag);
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartHeadList;
        };

        //获取求购尾货数据
        Callable<IPage> cargoListCallable = ()->{
            QueryWrapper<Cargo> cargoQueryWrapper = new QueryWrapper<>();
            IPage iPage = new Page(1,CARGO_NUMBER);
            cargoQueryWrapper.eq("delete_flag",0);
            cargoQueryWrapper.eq("cargo_flag",flag);
            cargoQueryWrapper.orderByDesc("update_time");
            IPage<Cargo> cargoIPage =  cargoService.page(iPage, cargoQueryWrapper);
            return cargoIPage;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<IPage> cargoTask = new FutureTask<>(cargoListCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(cargoTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
            entity.put("cargos", cargoTask.get());
            //缓存数据，没做先pass

        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            //重要！关闭创建的线程池
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }

    @GetMapping("/cargo")
    public Object cargo(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("flag") long flag, @RequestParam("current") long current) {
        //获取求购尾货数据
        QueryWrapper<Cargo> cargoQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,CARGO_NUMBER);
        cargoQueryWrapper.eq("delete_flag",0);
        cargoQueryWrapper.eq("cargo_flag",flag);
        cargoQueryWrapper.orderByDesc("update_time");
        IPage<Cargo> cargoIPage =  cargoService.page(iPage, cargoQueryWrapper);
        return ResponseUtil.ok(cargoIPage);
    }

    @GetMapping("/image")
    public Object image(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("id") long id) {
        //获取尾货求购轮播图数据
        QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
        imageChartQueryWrapper.eq("delete_flag",0);
        imageChartQueryWrapper.eq("cargo_id",id);
        List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
        return imageChartHeadList;
    }

}
