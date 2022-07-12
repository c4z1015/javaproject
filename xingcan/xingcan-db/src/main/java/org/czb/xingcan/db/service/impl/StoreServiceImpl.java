package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.ImageChartMapper;
import org.czb.xingcan.db.dao.StoreMapper;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.StoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Resource
    private ImageChartMapper imageChartMapper;

    @Resource
    private StoreMapper shopMapper;

    @Override
    public IPage<Store> queryStoresList(IPage iPage, QueryWrapper<Store> storeQueryWrapper) {
        IPage<Store> page = shopMapper.selectPage(iPage,storeQueryWrapper);
        for(Store store: page.getRecords()){
            QueryWrapper<ImageChart> imageChartQueryWrapper = new QueryWrapper<>();
            imageChartQueryWrapper.eq("store_id",store.getId());
            imageChartQueryWrapper.eq("delete_flag",0);
            List<ImageChart> imgs = imageChartMapper.selectList(imageChartQueryWrapper);
            store.setImgs(imgs);
        }
        return page;
    }
}
