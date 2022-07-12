package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.*;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderGoodMapper orderGoodMapper;

    @Resource
    private OrderPaymentMapper orderPaymentMapper;

    @Resource
    private SubGoodInfoMapper subGoodInfoMapper;

    @Resource
    private GoodSpecMapper goodSpecMapper;

    @Resource
    private OrderButtonMapper orderButtonMapper;

    @Override
    public void saveOrderGood(OrderGood orderGood) {
        orderGood.setAddTime(new Date());
        orderGood.setUpdateTime(new Date());
        orderGoodMapper.insert(orderGood);
    }

    @Override
    public void saveOrderPayment(OrderPayment orderPayment) {
        orderPayment.setAddTime(new Date());
        orderPayment.setUpdateTime(new Date());
        orderPaymentMapper.insert(orderPayment);
    }

    @Override
    public IPage<Order> queryOrdersList(IPage iPage, QueryWrapper<Order> qw) {
        IPage<Order> page = orderMapper.selectPage(iPage,qw);
        for (Order order : page.getRecords()){
            QueryWrapper<OrderGood> orderGoodQueryWrapper = new QueryWrapper<>();
            orderGoodQueryWrapper.eq("order_no",order.getOrderNo());
            List<OrderGood> orderGoodList = orderGoodMapper.selectList(orderGoodQueryWrapper);
            for(OrderGood good : orderGoodList){
                SubGoodInfo subGoodInfo = subGoodInfoMapper.selectById(good.getSkuId());
                good.setTitle(subGoodInfo.getTitle());
                good.setThumb(subGoodInfo.getPrimaryImage());
                QueryWrapper<GoodSpec> goodSpecQueryWrapper = new QueryWrapper<>();
                goodSpecQueryWrapper.eq("delete_flag",0);
                goodSpecQueryWrapper.eq("good_id",subGoodInfo.getId());
                List<GoodSpec> goodSpecList = goodSpecMapper.selectList(goodSpecQueryWrapper);
                List<String> specs = new ArrayList<>();
                for(GoodSpec goodSpec : goodSpecList){
                    specs.add(goodSpec.getSubSpecName());
                }
                good.setSpecs(specs);
            }
            QueryWrapper<OrderButton> orderButtonQueryWrapper = new QueryWrapper<>();
            orderButtonQueryWrapper.eq("delete_flag",0);
            orderButtonQueryWrapper.eq("order_status",order.getOrderStatus());
            List<OrderButton> orderButtons = orderButtonMapper.selectList(orderButtonQueryWrapper);
            for(OrderButton orderButton : orderButtons){
                orderButton.setPrimary(0==orderButton.getPrimaryStatus());
            }
            order.setButtons(orderButtons);
            order.setGoodsList(orderGoodList);
        }
        return page;
    }
}
