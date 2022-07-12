package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.CartService;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class WxCartController {
    private final Log logger = LogFactory.getLog(WxCartController.class);

    //默认店铺首页显示的商品数量
    private final static  Integer GOODS_NUMBER = 10;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Autowired
    private StoreService storeService;

    @Autowired
    private CartService cartService;

    @Autowired
    private GoodInfoService goodInfoService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId) {
        //优先获取缓存数据，没做先pass

        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(6);

        //获取购物车是否空车信息
        Callable<Boolean> cartEmptyCallable = ()->{
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("delete_flag",0);
            cartQueryWrapper.eq("open_id",openId);
            Long count =  cartService.count(cartQueryWrapper);
            if(count > 0){
                return true;
            }else{
                return false;
            }
        };

        //获取已经失效的购物车商品信息
        Callable<List> invalidGoodListCallable = ()->{
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("delete_flag",0);
            cartQueryWrapper.eq("open_id",openId);
            List<Store> subGoodInfoList =  cartService.queryInvalidGoodList(cartQueryWrapper);
            return subGoodInfoList;
        };

        //获取购物车商品信息
        Callable<List> goodListCallable = ()->{
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("delete_flag",0);
            cartQueryWrapper.eq("open_id",openId);
            List<Store> subGoodInfoList =  cartService.queryGoodList(cartQueryWrapper);
            return subGoodInfoList;
        };

        //获取购物车是否全选信息
        Callable<Boolean> cartSelectedCallable = ()->{
            Boolean isAllSelected = true;
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("delete_flag",0);
            cartQueryWrapper.eq("open_id",openId);
            List<Cart> carts =  cartService.list(cartQueryWrapper);
            for(Cart cart : carts){
                if(cart.getIsSelected() != 0){
                    isAllSelected = false;
                    break;
                }
            }
            return isAllSelected;
        };

        //获取购物车选中商品购买数量信息
        Callable<Integer> selectedGoodsCountCallable = ()->{
            Integer selectedGoodsCount = 0;
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("delete_flag",0);
            cartQueryWrapper.eq("open_id",openId);
            List<Cart> carts =  cartService.list(cartQueryWrapper);
            for(Cart cart : carts){
                if(cart.getIsSelected() == 0){
                    selectedGoodsCount += cart.getBuyQuantity();
                }
            }
            return selectedGoodsCount;
        };

        //获取购物车选中商品总价信息
        Callable<Map> selectedTotalAmountCallable = ()->{
            Map<String , Integer> totalAmount = cartService.querySelectedTotalAmount(openId);
            return totalAmount;
        };

        //加入线程池任务队列
        FutureTask<Boolean> cartEmptyTask = new FutureTask<>(cartEmptyCallable);
        FutureTask<List> invalidGoodTask = new FutureTask<>(invalidGoodListCallable);
        FutureTask<List> goodTask = new FutureTask<>(goodListCallable);
        FutureTask<Boolean> cartSelectedTask = new FutureTask<>(cartSelectedCallable);
        FutureTask<Integer> selectedGoodsCountTask = new FutureTask<>(selectedGoodsCountCallable);
        FutureTask<Map> selectedTotalAmountTask = new FutureTask<>(selectedTotalAmountCallable);

        //提交任务队列
        executorService.submit(cartEmptyTask);
        executorService.submit(invalidGoodTask);
        executorService.submit(goodTask);
        executorService.submit(cartSelectedTask);
        executorService.submit(selectedGoodsCountTask);
        executorService.submit(selectedTotalAmountTask);



        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("isNotEmpty", cartEmptyTask.get());
            entity.put("invalidGood", invalidGoodTask.get());
            entity.put("good", goodTask.get());
            entity.put("isAllSelected", cartSelectedTask.get());
            entity.put("selectedGoodsCount",selectedGoodsCountTask.get());
            entity.put("amount",selectedTotalAmountTask.get());
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

    @PostMapping("/saveOrUpdate")
    public Object saveOrUpdate(@RequestHeader("X-WX-OPENID") String openId , @RequestBody Cart cart) {
        try {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("open_id",openId);
            cartQueryWrapper.eq("store_id",cart.getStoreId());
            cartQueryWrapper.eq("sub_good_id",cart.getSubGoodId());
            Cart newCart = cartService.getOne(cartQueryWrapper);
            if(newCart!=null){
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("delete_flag",0);
                cartUpdateWrapper.set("is_selected",0);
                cartUpdateWrapper.set("buy_quantity",cart.getBuyQuantity());
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("id",newCart.getId());
                cartService.update(cartUpdateWrapper);
            }else{
                cart.setOpenId(openId);
                cart.setAddTime(new Date());
                cart.setUpdateTime(new Date());
                cartService.save(cart);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/storeSelected")
    public Object storeSelected(@RequestHeader("X-WX-OPENID") String openId , @RequestBody CartBody cart) {
        try {
            if(cart.getIsSelected()){
                //检查库存后再修改
                cartService.changeStoreSelected(openId ,cart.getStoreId());
            }else {
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("is_selected",1);
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("store_id",cart.getStoreId());
                cartUpdateWrapper.eq("open_id",openId);
                cartUpdateWrapper.eq("delete_flag",0);
                cartService.update(cartUpdateWrapper);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/goodSelected")
    public Object goodSelected(@RequestHeader("X-WX-OPENID") String openId , @RequestBody CartBody cart) {
        try {
            if(cart.getIsSelected()){
                //检查库存后再修改
                cartService.changeGoodSelected(openId ,cart.getSubGoodId());
            }else {
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("is_selected",1);
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("sub_good_id",cart.getSubGoodId());
                cartUpdateWrapper.eq("open_id",openId);
                cartUpdateWrapper.eq("delete_flag",0);
                cartService.update(cartUpdateWrapper);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/allSelected")
    public Object allSelected(@RequestHeader("X-WX-OPENID") String openId , @RequestBody CartBody cart) {
        try {
            if(cart.getIsSelected()){
                //检查库存后再修改
                cartService.changeAllSelected(openId);
            }else {
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("is_selected",1);
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("open_id",openId);
                cartUpdateWrapper.eq("delete_flag",0);
                cartService.update(cartUpdateWrapper);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/goodChangeNum")
    public Object goodChangeNum(@RequestHeader("X-WX-OPENID") String openId , @RequestBody CartBody cart) {
        try {
            UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
            cartUpdateWrapper.set("buy_quantity",cart.getBuyQuantity());
            cartUpdateWrapper.set("update_time",new Date());
            cartUpdateWrapper.eq("sub_good_id",cart.getSubGoodId());
            cartUpdateWrapper.eq("open_id",openId);
            cartUpdateWrapper.eq("delete_flag",0);
            cartService.update(cartUpdateWrapper);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/goodDeleted")
    public Object goodDeleted(@RequestHeader("X-WX-OPENID") String openId , @RequestBody CartBody cart) {
        try {
            UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
            cartUpdateWrapper.set("delete_flag",1);
            cartUpdateWrapper.set("update_time",new Date());
            cartUpdateWrapper.eq("sub_good_id",cart.getSubGoodId());
            cartUpdateWrapper.eq("open_id",openId);
            cartUpdateWrapper.eq("delete_flag",0);
            cartService.update(cartUpdateWrapper);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }
}
