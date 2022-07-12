package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.domain.Store;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.ImageChartService;
import org.czb.xingcan.db.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/store")
public class WxStoreController {
    private final Log logger = LogFactory.getLog(WxStoreController.class);

    //默认店铺首页显示的商品数量
    private final static  Integer GOODS_NUMBER = 10;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private StoreService storeService;

    @Autowired
    private ImageChartService imageChartService;

    @Autowired
    private GoodInfoService goodInfoService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId,@RequestParam Integer id) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        //获取店铺信息
        Callable<Store> storeCallable = ()->{
            Store store =  storeService.getById(id);
            return store;
        };

        //获取店铺商品信息
        Callable<IPage> goodsListCallable = ()->{
            QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
            IPage iPage = new Page(1,GOODS_NUMBER);
            goodsQueryWrapper.eq("delete_flag",0);
            goodsQueryWrapper.eq("store_id",id);
            goodsQueryWrapper.orderByDesc("update_time");
            IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);
            return goodsList;
        };

        //加入线程池任务队列
        FutureTask<Store> storeFutureTask = new FutureTask<>(storeCallable);
        FutureTask<IPage> goodsTask = new FutureTask<>(goodsListCallable);

        //提交任务队列
        executorService.submit(storeFutureTask);
        executorService.submit(goodsTask);


        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("store", storeFutureTask.get());
            entity.put("good", goodsTask.get());
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
    public Object goods(@RequestHeader("X-WX-OPENID") String openId,@RequestParam("current") long current,@RequestParam("id") Integer id) {

        //获取店铺商品数据
        QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,GOODS_NUMBER);
        goodsQueryWrapper.eq("delete_flag",0);
        goodsQueryWrapper.eq("store_id",id);
        goodsQueryWrapper.orderByDesc("update_time");
        IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);

        return ResponseUtil.ok(goodsList);
    }

    @GetMapping("/listIndex")
    public Object listIndex(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("belong") String belong) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //获取店铺列表轮播图广告位数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("display_location","store-list-"+belong+"-head");
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartHeadList;
        };

        //获取店铺列表数据
        Callable<IPage> storesListCallable = ()->{
            QueryWrapper<Store> storeQueryWrapper = new QueryWrapper<>();
            IPage iPage = new Page(1,GOODS_NUMBER);
            storeQueryWrapper.eq("delete_flag",0);
            storeQueryWrapper.eq("belong",belong);
            storeQueryWrapper.orderByDesc("update_time");
            IPage<Store> storesList =  storeService.queryStoresList(iPage, storeQueryWrapper);
            return storesList;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<IPage> storesTask = new FutureTask<>(storesListCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(storesTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
            entity.put("stores", storesTask.get());
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

    @GetMapping("/listStores")
    public Object listStores(@RequestHeader("X-WX-OPENID") String openId,
                                @RequestParam("current") long current,
                                @RequestParam("belong") String belong,
                                @RequestParam("price") Integer price,
                                @RequestParam("all") Integer all,
                                @RequestParam("sorts") String sorts,
                                @RequestParam("min") Integer min,
                                @RequestParam("max") Integer max) {

        //获取店铺列表数据
        QueryWrapper<Store> storeQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,GOODS_NUMBER);
        storeQueryWrapper.eq("delete_flag",0);
        if(belong!=null){
            storeQueryWrapper.eq("belong",belong);
        }
        storeQueryWrapper.orderByDesc("update_time");
        IPage<Store> storesList =  storeService.queryStoresList(iPage, storeQueryWrapper);
        return ResponseUtil.ok(storesList);
    }
}
