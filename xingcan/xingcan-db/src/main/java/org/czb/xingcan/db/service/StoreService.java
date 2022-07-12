package org.czb.xingcan.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.czb.xingcan.db.domain.Store;

public interface StoreService extends IService<Store> {

    IPage<Store> queryStoresList(IPage iPage, QueryWrapper<Store> storeQueryWrapper);
}