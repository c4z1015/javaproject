package org.czb.xingcan.wx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.czb.xingcan.core.utils.ResponseUtil;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.CartService;
import org.czb.xingcan.db.service.GoodInfoService;
import org.czb.xingcan.db.service.OrderService;
import org.czb.xingcan.db.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/order")
public class WxOrderController {
    private final Log logger = LogFactory.getLog(WxOrderController.class);

    //默认显示的订单数量
    private final static  Integer ORDERS_NUMBER = 10;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("/index")
    public Object index(@RequestHeader("X-WX-OPENID") String openId) {
        return null;
    }

    @PostMapping("/confirm")
    public Object confirm(@RequestHeader("X-WX-OPENID") String openId , @RequestBody OrderConfirm confirm) {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<Boolean> orderCallable = ()->{
            UserAddress userAddress = confirm.getUserAddress();
            List<Store> storeList = confirm.getStores();
            Long time = System.currentTimeMillis();
            int i = 1;
            for(Store store : storeList){
                String orderNo = time + "" + i;
                int orderTotalAmount = 0;
                int orderPaymentAmount = 0;
                int orderDiscountAmount = 0;
                for(SubGoodInfo subGoodInfo : store.getSubGoodInfos()){
                    int totalAmount = subGoodInfo.getLinePrice();
                    int paymentAmount = subGoodInfo.getPrice();
                    int discountAmount = totalAmount - paymentAmount;
                    int buyQuantity = subGoodInfo.getBuyQuantity();
                    OrderGood orderGood = new OrderGood();
                    orderGood.setOrderNo(orderNo);
                    orderGood.setSpuId(subGoodInfo.getParentId());
                    orderGood.setSkuId(subGoodInfo.getId());
                    orderGood.setBuyQuantity(buyQuantity);
                    orderGood.setTotalAmount(totalAmount);
                    orderGood.setPaymentAmount(paymentAmount);
                    orderGood.setDiscountAmount(discountAmount);
                    orderService.saveOrderGood(orderGood);
                    QueryWrapper<Cart> qw = new QueryWrapper<>();
                    qw.eq("open_id",openId);
                    qw.eq("sub_good_id",subGoodInfo.getId());
                    cartService.remove(qw);
                    orderTotalAmount += totalAmount * buyQuantity;
                    orderPaymentAmount += paymentAmount * buyQuantity;
                    orderDiscountAmount += discountAmount * buyQuantity;
                }
                OrderPayment orderPayment = new OrderPayment();
                orderPayment.setOrderNo(orderNo);
                orderPayment.setAmount(orderPaymentAmount);
                orderPayment.setPayTime(new Date());
                orderService.saveOrderPayment(orderPayment);
                Order order = new Order();
                order.setOpenId(openId);
                order.setOrderNo(orderNo);
                order.setTotalAmount(orderTotalAmount);
                order.setPaymentAmount(orderPaymentAmount);
                order.setDiscountAmount(orderDiscountAmount);
                order.setFreightFee(0);
                order.setRemark(store.getRemark());
                order.setStoreId(store.getId());
                order.setStoreName(store.getTitle());
                order.setUserName(userAddress.getName());
                order.setUserPhone(userAddress.getPhone());
                order.setDetailAddress(userAddress.getProvinceName()+userAddress.getCityName()+userAddress.getDistrictName()+userAddress.getDetailAddress());
                order.setLatitude(userAddress.getLatitude());
                order.setLongitude(userAddress.getLongitude());
                order.setAddTime(new Date());
                order.setUpdateTime(new Date());
                orderService.save(order);
                i++;
            }
            return true;
        };
        FutureTask<Boolean> orderTask = new FutureTask<>(orderCallable);
        executorService.submit(orderTask);
        try {
             orderTask.get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            //重要！关闭创建的线程池
            executorService.shutdown();
        }
        return ResponseUtil.ok();
    }

    @GetMapping("/list")
    public Object list(@RequestHeader("X-WX-OPENID") String openId, @RequestParam("current") long current, @RequestParam("status") long status) {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        IPage iPage = new Page(current,ORDERS_NUMBER);
        if(0 < status){
            orderQueryWrapper.eq("order_status",status);
        }
        orderQueryWrapper.eq("delete_flag",0);
        orderQueryWrapper.eq("open_id",openId);
        orderQueryWrapper.orderByDesc("update_time");
        IPage<Order> orderIPage =  orderService.queryOrdersList(iPage, orderQueryWrapper);
        return ResponseUtil.ok(orderIPage);
    }
}
