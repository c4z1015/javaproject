package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.domain.SpecProperty;
import org.czb.xingcan.db.domain.SubGoodInfo;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.ImageChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/good")
public class WxGoodController {
    private final Log logger = LogFactory.getLog(WxGoodController.class);

    //首页显示的广告商品数量
    private final static  Integer AD_GOODS_NUMBER = 4;
    //默认首页显示的商品数量
    private final static  Integer GOODS_NUMBER = 10;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private ImageChartService imageChartService;

    @Autowired
    private GoodInfoService goodInfoService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId ,@RequestParam("id") Integer id) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        //获取商品基础数据
        Callable<GoodInfo> goodInfoCallable = ()->{
            GoodInfo goodInfo =  goodInfoService.getById(id);
            return goodInfo;
        };

        //获取商品图片数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("good_id",id);
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
            List<String> imageList = imageChartHeadList.stream().map(ImageChart::getUrl).collect(Collectors.toList());
            return imageList;
        };


        //获取商品详情图数据
        Callable<List> imageChartBodyListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.likeRight("info","good-"+id+"-");
            imageChartQueryWrapper.orderByAsc("info");
            List<ImageChart> imageChartBodyList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartBodyList;
        };

        //获取子商品数据
        Callable<List> subGoodInfoCallable = ()->{
            List<SubGoodInfo> subGoodInfoList =  goodInfoService.querySubGoodListByParentId(id);
            return subGoodInfoList;
        };

        //获取商品规格数据
        Callable<List> specPropertyCallable = ()->{
            List<SpecProperty> specPropertyList =  goodInfoService.querySpecPropertyListById(id);
            return specPropertyList;
        };

        //加入线程池任务队列
        FutureTask<GoodInfo> goodInfoTask = new FutureTask<>(goodInfoCallable);
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<List> imageChartBodyTask = new FutureTask<>(imageChartBodyListCallable);
        FutureTask<List> subGoodInfoTask = new FutureTask<>(subGoodInfoCallable);
        FutureTask<List> specPropertyTask = new FutureTask<>(specPropertyCallable);

        //提交任务队列
        executorService.submit(goodInfoTask);
        executorService.submit(imageChartHeadTask);
        executorService.submit(imageChartBodyTask);
        executorService.submit(subGoodInfoTask);
        executorService.submit(specPropertyTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("good", goodInfoTask.get());
            entity.put("goodImages", imageChartHeadTask.get());
            entity.put("goodDesc", imageChartBodyTask.get());
            entity.put("subGood", subGoodInfoTask.get());
            entity.put("goodSpec", specPropertyTask.get());
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
