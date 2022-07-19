package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.service.ImageChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping("/index")
public class WxIndexController {
    private final Log logger = LogFactory.getLog(WxIndexController.class);

    @Autowired
    private ImageChartService imageChartService;


    @GetMapping("/static")
    public Object index(@RequestHeader("X-WX-OPENID") String openId) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        //获取欢迎页轮播图数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartHeadQueryWrapper = new QueryWrapper<>();
            imageChartHeadQueryWrapper.eq("delete_flag",0);
            imageChartHeadQueryWrapper.eq("display_location","index-head-swiper");
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartHeadQueryWrapper);
            return imageChartHeadList;
        };

        //获取欢迎页宫格图数据
        Callable<List> imageChartBodyListCallable = ()->{
            QueryWrapper<ImageChart> imageChartBodyQueryWrapper = new QueryWrapper<>();
            imageChartBodyQueryWrapper.eq("delete_flag",0);
            imageChartBodyQueryWrapper.eq("display_location","index-body-grid");
            List<ImageChart> imageChartBodyList =  imageChartService.list(imageChartBodyQueryWrapper);
            return imageChartBodyList;
        };

        //获取欢迎页公司信息图数据
        Callable<ImageChart> imageChartBottomCallable = ()->{
            QueryWrapper<ImageChart> imageChartBottomQueryWrapper = new QueryWrapper<>();
            imageChartBottomQueryWrapper.eq("delete_flag",0);
            imageChartBottomQueryWrapper.eq("display_location","index-bottom-image");
            ImageChart imageChartBottom =  imageChartService.getOne(imageChartBottomQueryWrapper);
            return imageChartBottom;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<List> imageChartBodyTask = new FutureTask<>(imageChartBodyListCallable);
        FutureTask<ImageChart> imageChartBottomTask = new FutureTask<>(imageChartBottomCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(imageChartBodyTask);
        executorService.submit(imageChartBottomTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
            entity.put("imageChartBody", imageChartBodyTask.get());
            entity.put("imageChartBottom", imageChartBottomTask.get());
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
}
