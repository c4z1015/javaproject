package org.czb.xingcan.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.SpecProperty;
import org.czb.xingcan.db.domain.SubGoodInfo;

import java.util.List;

public interface GoodInfoService extends IService<GoodInfo> {

    List<GoodInfo> queryGoodsList(QueryWrapper<GoodInfo> qw, boolean b);

    IPage<GoodInfo> queryGoodsList(IPage iPage, QueryWrapper<GoodInfo> goodsQueryWrapper, boolean b);

    List<SubGoodInfo> querySubGoodListByParentId(Integer id);

    List<SpecProperty> querySpecPropertyListById(Integer id);
}
