package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.GoodInfoMapper;
import org.czb.xingcan.db.dao.GoodTagMapper;
import org.czb.xingcan.db.dao.TagMapper;
import org.czb.xingcan.db.domain.GoodInfo;
import org.czb.xingcan.db.domain.GoodTag;
import org.czb.xingcan.db.domain.Tag;
import org.czb.xingcan.db.service.GoodInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodInfoServiceImpl extends ServiceImpl<GoodInfoMapper, GoodInfo> implements GoodInfoService {

    @Resource
    private GoodInfoMapper goodInfoMapper;

    @Resource
    private GoodTagMapper goodTagMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public List<GoodInfo> queryGoodsList(QueryWrapper<GoodInfo> qw, boolean b) {
        List<GoodInfo> goodInfoList = goodInfoMapper.selectList(qw);
        for(GoodInfo goodInfo: goodInfoList){
            List<Tag> tags = new ArrayList<>();
            if(b){
                tags.add(new Tag("广告"));
            }
            QueryWrapper<GoodTag> goodTagQueryWrapper = new QueryWrapper<>();
            goodTagQueryWrapper.eq("good_id",goodInfo.getId());
            goodTagQueryWrapper.eq("delete_flag",0);
            List<GoodTag> goodTagList = goodTagMapper.selectList(goodTagQueryWrapper);
            for(GoodTag goodTag: goodTagList){
                Tag tag = tagMapper.selectById(goodTag.getTagId());
                tags.add(tag);
            }
            goodInfo.setTags(tags);
        }
        return goodInfoList;
    }

    @Override
    public IPage<GoodInfo> queryGoodsList(IPage iPage, QueryWrapper<GoodInfo> qw, boolean b) {
        IPage<GoodInfo> page = goodInfoMapper.selectPage(iPage,qw);
        for(GoodInfo goodInfo: page.getRecords()){
            List<Tag> tags = new ArrayList<>();
            QueryWrapper<GoodTag> goodTagQueryWrapper = new QueryWrapper<>();
            goodTagQueryWrapper.eq("good_id",goodInfo.getId());
            goodTagQueryWrapper.eq("delete_flag",0);
            List<GoodTag> goodTagList = goodTagMapper.selectList(goodTagQueryWrapper);
            for(GoodTag goodTag: goodTagList){
                Tag tag = tagMapper.selectById(goodTag.getTagId());
                tags.add(tag);
            }
            goodInfo.setTags(tags);
        }
        return page;
    }
}
