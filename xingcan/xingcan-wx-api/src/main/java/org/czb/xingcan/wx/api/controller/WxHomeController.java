package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.ImageChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private ImageChartService imageChartService;

    @Autowired
    private GoodInfoService goodInfoService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        //获取首页轮播图广告位数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("display_location","supply-home-head");
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartHeadList;
        };


        //获取首页宫格栏左侧轮播图广告位数据
        Callable<List> imageChartGridListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("display_location","supply-home-grid-left");
            List<ImageChart> imageChartGridList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartGridList;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<List> imageChartGridTask = new FutureTask<>(imageChartGridListCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(imageChartGridTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
            entity.put("imageChartGrid", imageChartGridTask.get());
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

    @GetMapping("/goods")
    public Object goods(@RequestHeader("X-WX-OPENID") String openId,@RequestParam("current") long current,@RequestParam("size") long size) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        if(current == 1){
            //获取首页广告商品数据
            Callable<List> adGoodsListCallable = ()->{
                QueryWrapper<GoodInfo> adGoodsQueryWrapper = new QueryWrapper<>();
                adGoodsQueryWrapper.eq("delete_flag",0);
                adGoodsQueryWrapper.eq("ad_flag",1);
                adGoodsQueryWrapper.last("limit 4");
                adGoodsQueryWrapper.orderByDesc("update_time");
                List<GoodInfo> adGoodsList =  goodInfoService.queryGoodsList(adGoodsQueryWrapper,true);
                return adGoodsList;
            };

            //获取首页商品数据
            Callable<IPage> goodsListCallable = ()->{
                QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
                IPage iPage = new Page(current,size);
                goodsQueryWrapper.eq("delete_flag",0);
                goodsQueryWrapper.orderByDesc("update_time");
                IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);
                return goodsList;
            };

            //加入线程池任务队列
            FutureTask<List> adGoodsTask = new FutureTask<>(adGoodsListCallable);
            FutureTask<IPage> goodsTask = new FutureTask<>(goodsListCallable);

            //提交任务队列
            executorService.submit(adGoodsTask);
            executorService.submit(goodsTask);

            Map<String, Object> entity = new HashMap<>();
            try {
                entity.put("adGoods", adGoodsTask.get());
                entity.put("goods", goodsTask.get());
                //缓存数据，没做先pass

            }
            catch (Exception e) {
                e.printStackTrace();
            }finally {
                //重要！关闭创建的线程池
                executorService.shutdown();
            }
            return ResponseUtil.ok(entity);
        }else{

            //获取首页商品数据
            Callable<IPage> goodsListCallable = ()->{
                QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
                IPage iPage = new Page(current,size);
                goodsQueryWrapper.eq("delete_flag",0);
                goodsQueryWrapper.orderByDesc("update_time");
                IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);
                return goodsList;
            };

            //加入线程池任务队列
            FutureTask<IPage> goodsTask = new FutureTask<>(goodsListCallable);

            //提交任务队列
            executorService.submit(goodsTask);

            Map<String, Object> entity = new HashMap<>();
            try {
                entity.put("goods", goodsTask.get());
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
}
