package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.*;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.GoodInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class GoodInfoServiceImpl extends ServiceImpl<GoodInfoMapper, GoodInfo> implements GoodInfoService {

    @Resource
    private GoodInfoMapper goodInfoMapper;

    @Resource
    private GoodTagMapper goodTagMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private SubGoodInfoMapper subGoodInfoMapper;

    @Resource
    private GoodSpecMapper goodSpecMapper;

    @Resource
    private GoodPriceMapper goodPriceMapper;

    @Resource
    private GoodStockMapper goodStockMapper;


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

    @Override
    public List<SubGoodInfo> querySubGoodListByParentId(Integer id) {
        QueryWrapper<SubGoodInfo> subGoodInfoQueryWrapper = new QueryWrapper<>();
        subGoodInfoQueryWrapper.eq("parent_id",id);
        subGoodInfoQueryWrapper.eq("delete_flag",0);
        List<SubGoodInfo> subGoodInfoList = subGoodInfoMapper.selectList(subGoodInfoQueryWrapper);
        for(SubGoodInfo subGoodInfo : subGoodInfoList){

            QueryWrapper<GoodSpec> goodSpecQueryWrapper = new QueryWrapper<>();
            goodSpecQueryWrapper.eq("good_id",subGoodInfo.getId());
            goodSpecQueryWrapper.eq("delete_flag",0);
            List<GoodSpec> goodSpecList = goodSpecMapper.selectList(goodSpecQueryWrapper);

            QueryWrapper<GoodPrice> goodPriceQueryWrapper = new QueryWrapper<>();
            goodPriceQueryWrapper.eq("good_id",subGoodInfo.getId());
            goodPriceQueryWrapper.eq("delete_flag",0);
            List<GoodPrice> goodPriceList = goodPriceMapper.selectList(goodPriceQueryWrapper);

            QueryWrapper<GoodStock> goodStockQueryWrapper = new QueryWrapper<>();
            goodStockQueryWrapper.eq("good_id",subGoodInfo.getId());
            goodStockQueryWrapper.eq("delete_flag",0);
            GoodStock goodStock = goodStockMapper.selectOne(goodStockQueryWrapper);

            for(GoodPrice goodPrice : goodPriceList){
                if(goodPrice.getPriceType() == 1){
                    subGoodInfo.setPrice(goodPrice.getPrice());
                }
                if(goodPrice.getPriceType() == 2){
                    subGoodInfo.setLinePrice(goodPrice.getPrice());
                }
            }
            subGoodInfo.setGoodSpecs(goodSpecList);
            subGoodInfo.setGoodPrices(goodPriceList);
            subGoodInfo.setGoodStock(goodStock);
        }
        return subGoodInfoList;
    }

    @Override
    public List<SpecProperty> querySpecPropertyListById(Integer id) {
        List<GoodSpec> goodSpecs = new ArrayList<>();
        QueryWrapper<SubGoodInfo> subGoodInfoQueryWrapper = new QueryWrapper<>();
        subGoodInfoQueryWrapper.eq("parent_id",id);
        subGoodInfoQueryWrapper.eq("delete_flag",0);
        List<SubGoodInfo> subGoodInfoList = subGoodInfoMapper.selectList(subGoodInfoQueryWrapper);
        for(SubGoodInfo subGoodInfo : subGoodInfoList){
            QueryWrapper<GoodSpec> goodSpecQueryWrapper = new QueryWrapper<>();
            goodSpecQueryWrapper.eq("good_id",subGoodInfo.getId());
            goodSpecQueryWrapper.eq("delete_flag",0);
            List<GoodSpec> goodSpecList = goodSpecMapper.selectList(goodSpecQueryWrapper);
            goodSpecs.addAll(goodSpecList);
        }
        List<SpecProperty> specHeads = new ArrayList<>();
        Map<String,Integer> check = new HashMap<>();
        for(GoodSpec goodSpec : goodSpecs){
            if(check!=null&&check.containsKey(goodSpec.getSpecName())){
                if(check!=null&&check.containsKey(goodSpec.getSubSpecName())){
                    continue;
                }
                SpecProperty specSecond = new SpecProperty();
                specSecond.setId(goodSpec.getSubSpecId());
                specSecond.setName(goodSpec.getSubSpecName());
                for(SpecProperty specProperty : specHeads){
                    if(goodSpec.getSpecName().equals(specProperty.getName())){
                        specProperty.getSpecPropertyList().add(specSecond);
                    }else{
                        continue;
                    }
                }
                check.put(goodSpec.getSubSpecName(),goodSpec.getSubSpecId());
                continue;
            }
            SpecProperty specFirst = new SpecProperty();
            specFirst.setId(goodSpec.getSpecId());
            specFirst.setName(goodSpec.getSpecName());
            List<SpecProperty> specSecondList = new ArrayList<>();
            SpecProperty specSecond = new SpecProperty();
            specSecond.setId(goodSpec.getSubSpecId());
            specSecond.setName(goodSpec.getSubSpecName());
            specSecondList.add(specSecond);
            specFirst.setSpecPropertyList(specSecondList);
            specHeads.add(specFirst);
            check.put(goodSpec.getSpecName(),goodSpec.getSpecId());
            check.put(goodSpec.getSubSpecName(),goodSpec.getSubSpecId());
        }
        return specHeads;
    }
}
