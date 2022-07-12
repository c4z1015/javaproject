package org.czb.xingcan.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.czb.xingcan.db.domain.Order;
import org.czb.xingcan.db.domain.OrderGood;
import org.czb.xingcan.db.domain.OrderPayment;

public interface OrderService extends IService<Order> {

    void saveOrderGood(OrderGood orderGood);

    void saveOrderPayment(OrderPayment orderPayment);

    IPage<Order> queryOrdersList(IPage iPage, QueryWrapper<Order> orderQueryWrapper);
}