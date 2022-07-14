package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.Cart;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.domain.SearchHistory;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.ImageChartService;
import org.czb.xingcan.db.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    //首页显示的广告商品数量
    private final static  Integer AD_GOODS_NUMBER = 4;
    //默认首页显示的商品数量
    private final static  Integer GOODS_NUMBER = 10;
    //默认首页显示的最大搜索展示数量
    private final static  Integer SEARCH_NUMBER = 20;

    @Autowired
    private ImageChartService imageChartService;

    @Autowired
    private GoodInfoService goodInfoService;

    @Autowired
    private SearchHistoryService searchHistoryService;


    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(4);

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

        //获取首页广告商品数据
        Callable<List> adGoodsListCallable = ()->{
            QueryWrapper<GoodInfo> adGoodsQueryWrapper = new QueryWrapper<>();
            adGoodsQueryWrapper.eq("delete_flag",0);
            adGoodsQueryWrapper.eq("ad_flag",1);
            adGoodsQueryWrapper.last("limit "+AD_GOODS_NUMBER);
            adGoodsQueryWrapper.orderByDesc("update_time");
            List<GoodInfo> adGoodsList =  goodInfoService.queryGoodsList(adGoodsQueryWrapper,true);
            return adGoodsList;
        };

        //获取首页商品数据
        Callable<IPage> goodsListCallable = ()->{
            QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
            IPage iPage = new Page(1,GOODS_NUMBER);
            goodsQueryWrapper.eq("delete_flag",0);
            goodsQueryWrapper.orderByDesc("update_time");
            IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);
            return goodsList;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<List> imageChartGridTask = new FutureTask<>(imageChartGridListCallable);
        FutureTask<List> adGoodsTask = new FutureTask<>(adGoodsListCallable);
        FutureTask<IPage> goodsTask = new FutureTask<>(goodsListCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(imageChartGridTask);
        executorService.submit(adGoodsTask);
        executorService.submit(goodsTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
            entity.put("imageChartGrid", imageChartGridTask.get());
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
    }

    @GetMapping("/goods")
    public Object goods(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("current") long current) {

        //获取首页商品数据
        QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,GOODS_NUMBER);
        goodsQueryWrapper.eq("delete_flag",0);
        goodsQueryWrapper.orderByDesc("update_time");
        IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);

        return ResponseUtil.ok(goodsList);
    }

    @GetMapping("/category")
    public Object category(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("flag") Integer flag) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //获取商品分类轮播图广告位数据
        Callable<List> imageChartHeadListCallable = ()->{
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("delete_flag",0);
            imageChartQueryWrapper.eq("display_location","supply-category-"+flag+"-head");
            List<ImageChart> imageChartHeadList =  imageChartService.list(imageChartQueryWrapper);
            return imageChartHeadList;
        };

        //获取商品分类商品数据
        Callable<IPage> goodsListCallable = ()->{
            QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
            IPage iPage = new Page(1,GOODS_NUMBER);
            goodsQueryWrapper.eq("delete_flag",0);
            goodsQueryWrapper.eq("grid_flag",flag);
            goodsQueryWrapper.orderByDesc("update_time");
            IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);
            return goodsList;
        };

        //加入线程池任务队列
        FutureTask<List> imageChartHeadTask = new FutureTask<>(imageChartHeadListCallable);
        FutureTask<IPage> goodsTask = new FutureTask<>(goodsListCallable);

        //提交任务队列
        executorService.submit(imageChartHeadTask);
        executorService.submit(goodsTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("imageChartHead", imageChartHeadTask.get());
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

    @GetMapping("/categoryGoods")
    public Object categoryGoods(@RequestHeader("X-WX-OPENID") String openId,
                        @RequestParam("current") long current,
                        @RequestParam("flag") Integer flag,
                        @RequestParam("price") Integer price,
                        @RequestParam("all") Integer all,
                        @RequestParam("sorts") String sorts,
                        @RequestParam("min") Integer min,
                        @RequestParam("max") Integer max, @RequestParam("keywords") String keywords) {

        //获取首页商品数据
        QueryWrapper<GoodInfo> goodsQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,GOODS_NUMBER);
        goodsQueryWrapper.eq("delete_flag",0);
        if(flag!=null && flag >0){
            goodsQueryWrapper.eq("grid_flag",flag);
        }
        if(min > 0){
            goodsQueryWrapper.gt("min_sale_price",min);
        }
        if(max > 0){
            goodsQueryWrapper.lt("min_sale_price",max);
        }
        if(all!=null&&all==1){
            goodsQueryWrapper.orderByDesc("update_time");
        }else if(price!=null&&price==1){
            if("asc".equals(sorts)){
                goodsQueryWrapper.orderByAsc("min_sale_price");
            }else{
                goodsQueryWrapper.orderByDesc("min_sale_price");
            }
        }
        if(keywords!=null && keywords.length()>0){
            goodsQueryWrapper.like("title",keywords);
        }
        IPage<GoodInfo> goodsList =  goodInfoService.queryGoodsList(iPage, goodsQueryWrapper, false);

        return ResponseUtil.ok(goodsList);
    }

    @GetMapping("/search")
    public Object search(@RequestHeader("X-WX-OPENID") String openId) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //获取用户历史搜索数据
        Callable<List> searchKeywordCallable = ()->{
            QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
            searchHistoryQueryWrapper.eq("delete_flag",0);
            searchHistoryQueryWrapper.eq("open_id",openId);
            searchHistoryQueryWrapper.orderByDesc("update_time");
            searchHistoryQueryWrapper.last("limit "+SEARCH_NUMBER);
            List<SearchHistory> searchHistoryList =  searchHistoryService.list(searchHistoryQueryWrapper);
            List<String> searchKeyword = searchHistoryList.stream().map(search -> search.getKeyword()).collect(Collectors.toList());
            return searchKeyword;
        };


        //获取热门历史搜索数据
        Callable<List> searchHotKeywordCallable = ()->{
            QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
            searchHistoryQueryWrapper.select("keyword");
            searchHistoryQueryWrapper.orderByDesc("IFNULL(sum(frequency),0)");
            searchHistoryQueryWrapper.groupBy("keyword");
            searchHistoryQueryWrapper.last("limit "+SEARCH_NUMBER);
            List<SearchHistory> searchHistoryList =  searchHistoryService.list(searchHistoryQueryWrapper);
            List<String> searchKeyword = searchHistoryList.stream().map(search -> search.getKeyword()).collect(Collectors.toList());
            return searchKeyword;
        };


        //加入线程池任务队列
        FutureTask<List> searchKeywordTask = new FutureTask<>(searchKeywordCallable);
        FutureTask<List> searchHotKeywordTask = new FutureTask<>(searchHotKeywordCallable);

        //提交任务队列
        executorService.submit(searchKeywordTask);
        executorService.submit(searchHotKeywordTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("searchKeyword", searchKeywordTask.get());
            entity.put("searchHotKeyword", searchHotKeywordTask.get());
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

    @PostMapping("/searchConfirm")
    public Object searchConfirm(@RequestHeader("X-WX-OPENID") String openId , @RequestBody SearchHistory sh) {
        try {
            QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
            searchHistoryQueryWrapper.eq("open_id",openId);
            searchHistoryQueryWrapper.eq("keyword",sh.getKeyword());
            SearchHistory searchHistory = searchHistoryService.getOne(searchHistoryQueryWrapper);
            if(searchHistory!=null){
                UpdateWrapper<SearchHistory> searchHistoryUpdateWrapper = new UpdateWrapper<>();
                searchHistoryUpdateWrapper.set("delete_flag",0);
                searchHistoryUpdateWrapper.set("frequency",searchHistory.getFrequency()+1);
                searchHistoryUpdateWrapper.set("update_time",new Date());
                searchHistoryUpdateWrapper.eq("id",searchHistory.getId());
                searchHistoryService.update(searchHistoryUpdateWrapper);
            }else{
                searchHistory = new SearchHistory();
                searchHistory.setOpenId(openId);
                searchHistory.setAddTime(new Date());
                searchHistory.setUpdateTime(new Date());
                searchHistory.setFrequency(1);
                searchHistory.setKeyword(sh.getKeyword());
                searchHistoryService.save(searchHistory);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/searchDelete")
    public Object searchDelete(@RequestHeader("X-WX-OPENID") String openId , @RequestBody SearchHistory sh) {
        try {
            if(sh.getDeleteFlag()!=null&&sh.getDeleteFlag()==0){
                UpdateWrapper<SearchHistory> searchHistoryUpdateWrapper = new UpdateWrapper<>();
                searchHistoryUpdateWrapper.set("delete_flag",1);
                searchHistoryUpdateWrapper.set("update_time",new Date());
                searchHistoryUpdateWrapper.eq("keyword",sh.getKeyword());
                searchHistoryUpdateWrapper.eq("open_id",openId);
                searchHistoryService.update(searchHistoryUpdateWrapper);
            }else{
                UpdateWrapper<SearchHistory> searchHistoryUpdateWrapper = new UpdateWrapper<>();
                searchHistoryUpdateWrapper.set("delete_flag",1);
                searchHistoryUpdateWrapper.set("update_time",new Date());
                searchHistoryUpdateWrapper.eq("open_id",openId);
                searchHistoryService.update(searchHistoryUpdateWrapper);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
        searchHistoryQueryWrapper.eq("delete_flag",0);
        searchHistoryQueryWrapper.eq("open_id",openId);
        searchHistoryQueryWrapper.orderByDesc("update_time");
        searchHistoryQueryWrapper.last("limit "+SEARCH_NUMBER);
        List<SearchHistory> searchHistoryList =  searchHistoryService.list(searchHistoryQueryWrapper);
        List<String> searchKeyword = searchHistoryList.stream().map(search -> search.getKeyword()).collect(Collectors.toList());
        return ResponseUtil.ok(searchKeyword);
    }
}
