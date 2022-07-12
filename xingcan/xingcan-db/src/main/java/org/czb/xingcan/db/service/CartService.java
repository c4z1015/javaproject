package org.czb.xingcan.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.czb.xingcan.db.domain.Cart;
import org.czb.xingcan.db.domain.Store;
import org.czb.xingcan.db.domain.SubGoodInfo;

import java.util.List;
import java.util.Map;

public interface CartService extends IService<Cart> {

    List<Store> queryInvalidGoodList(QueryWrapper<Cart> cartQueryWrapper);

    List<Store> queryGoodList(QueryWrapper<Cart> cartQueryWrapper);

    Map<String , Integer> querySelectedTotalAmount(String openId);

    void changeStoreSelected(String openId, Integer storeId);

    void changeGoodSelected(String openId, Integer subGoodId);

    void changeAllSelected(String openId);
}