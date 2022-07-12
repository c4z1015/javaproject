package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.*;
import org.czb.xingcan.db.domain.*;
import org.czb.xingcan.db.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private SubGoodInfoMapper subGoodInfoMapper;

    @Resource
    private GoodSpecMapper goodSpecMapper;

    @Resource
    private GoodPriceMapper goodPriceMapper;

    @Resource
    private GoodStockMapper goodStockMapper;

    @Override
    public List<Store> queryInvalidGoodList(QueryWrapper<Cart> cartQueryWrapper) {
        List<Store> storeList = new ArrayList<>();
        List<Cart>  cartList = cartMapper.selectList(cartQueryWrapper);
        //同一个店铺的商品放一起
        Map<Integer,List<Cart>> listMap = cartList.stream().collect(Collectors.groupingBy(Cart::getStoreId));
        for(Integer storeId : listMap.keySet()){
            List<SubGoodInfo> subGoodInfoList = new ArrayList<>();
            listMap.get(storeId).forEach(cart -> {
                QueryWrapper<SubGoodInfo> subGoodInfoQueryWrapper = new QueryWrapper<>();
                subGoodInfoQueryWrapper.eq("id",cart.getSubGoodId());
                subGoodInfoQueryWrapper.eq("delete_flag",1);
                SubGoodInfo subGoodInfo = subGoodInfoMapper.selectOne(subGoodInfoQueryWrapper);
                if(subGoodInfo != null){
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
                    subGoodInfo.setBuyQuantity(cart.getBuyQuantity());
                    subGoodInfo.setIsSelected(cart.getIsSelected());
                    subGoodInfo.setGoodSpecs(goodSpecList);
                    subGoodInfo.setGoodPrices(goodPriceList);
                    subGoodInfo.setGoodStock(goodStock);

                    subGoodInfoList.add(subGoodInfo);
                }
            });
            if(subGoodInfoList.size()>0){
                Store store = storeMapper.selectById(storeId);
                store.setSubGoodInfos(subGoodInfoList);
                storeList.add(store);
            }
        }
        return storeList;
    }

    @Override
    public List<Store> queryGoodList(QueryWrapper<Cart> cartQueryWrapper) {
        List<Store> storeList = new ArrayList<>();
        List<Cart>  cartList = cartMapper.selectList(cartQueryWrapper);
        //同一个店铺的商品放一起
        Map<Integer,List<Cart>> listMap = cartList.stream().collect(Collectors.groupingBy(Cart::getStoreId));
        for(Integer storeId : listMap.keySet()){
            List<SubGoodInfo> subGoodInfoList = new ArrayList<>();
            listMap.get(storeId).forEach(cart -> {
                QueryWrapper<SubGoodInfo> subGoodInfoQueryWrapper = new QueryWrapper<>();
                subGoodInfoQueryWrapper.eq("id",cart.getSubGoodId());
                subGoodInfoQueryWrapper.eq("delete_flag",0);
                SubGoodInfo subGoodInfo = subGoodInfoMapper.selectOne(subGoodInfoQueryWrapper);
                if(subGoodInfo != null){
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
                    subGoodInfo.setBuyQuantity(cart.getBuyQuantity());
                    subGoodInfo.setIsSelected(cart.getIsSelected());
                    subGoodInfo.setGoodSpecs(goodSpecList);
                    subGoodInfo.setGoodPrices(goodPriceList);
                    subGoodInfo.setGoodStock(goodStock);

                    subGoodInfoList.add(subGoodInfo);
                }
            });
            if(subGoodInfoList.size()>0){
                Store store = storeMapper.selectById(storeId);
                store.setSubGoodInfos(subGoodInfoList);
                storeList.add(store);
            }
        }
        return storeList;
    }

    @Override
    public Map<String , Integer> querySelectedTotalAmount(String openId) {
        Integer selectedTotalAmount = 0;
        Integer totalDiscountAmount = 0;
        Map<String , Integer> amount = new HashMap<>();
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("delete_flag",0);
        cartQueryWrapper.eq("open_id",openId);
        List<Cart> carts =  cartMapper.selectList(cartQueryWrapper);
        List<Cart> selectedCarts = carts.stream().filter(cart -> cart.getIsSelected() == 0).collect(Collectors.toList());
        for(Cart cart : selectedCarts){
            QueryWrapper<GoodPrice> goodPriceQueryWrapper = new QueryWrapper<>();
            goodPriceQueryWrapper.eq("good_id",cart.getSubGoodId());
            goodPriceQueryWrapper.eq("delete_flag",0);
            List<GoodPrice> goodPriceList = goodPriceMapper.selectList(goodPriceQueryWrapper);
            Integer salePrice = 0;
            Integer linePirce = 0;
            for(GoodPrice price : goodPriceList){
                if(price.getPriceType() == 1){
                    salePrice = price.getPrice();
                }
                if(price.getPriceType() == 2){
                    linePirce = price.getPrice();
                }
            }
            selectedTotalAmount += salePrice * cart.getBuyQuantity();
            totalDiscountAmount += (linePirce - salePrice) * cart.getBuyQuantity();
        }
        amount.put("selectedTotalAmount",selectedTotalAmount);
        amount.put("totalDiscountAmount",totalDiscountAmount);
        return amount;
    }

    @Override
    public void changeStoreSelected(String openId, Integer storeId) {
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("open_id",openId);
        cartQueryWrapper.eq("store_id",storeId);
        cartQueryWrapper.eq("delete_flag",0);
        List<Cart> carts =  cartMapper.selectList(cartQueryWrapper);
        for(Cart cart : carts){
            QueryWrapper<GoodStock> goodStockQueryWrapper = new QueryWrapper<>();
            goodStockQueryWrapper.eq("good_id",cart.getSubGoodId());
            goodStockQueryWrapper.eq("delete_flag",0);
            GoodStock goodStock = goodStockMapper.selectOne(goodStockQueryWrapper);
            if(goodStock != null && goodStock.getStockQuantity() > 0){
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("is_selected",0);
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("sub_good_id",cart.getSubGoodId());
                cartUpdateWrapper.eq("open_id",openId);
                cartUpdateWrapper.eq("delete_flag",0);
                cartMapper.update(cart ,cartUpdateWrapper);
            }
        }
    }

    @Override
    public void changeGoodSelected(String openId, Integer subGoodId) {
        QueryWrapper<GoodStock> goodStockQueryWrapper = new QueryWrapper<>();
        goodStockQueryWrapper.eq("good_id",subGoodId);
        goodStockQueryWrapper.eq("delete_flag",0);
        GoodStock goodStock = goodStockMapper.selectOne(goodStockQueryWrapper);
        if(goodStock != null && goodStock.getStockQuantity() > 0){
            UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
            cartUpdateWrapper.set("is_selected",0);
            cartUpdateWrapper.set("update_time",new Date());
            cartUpdateWrapper.eq("sub_good_id",subGoodId);
            cartUpdateWrapper.eq("open_id",openId);
            cartUpdateWrapper.eq("delete_flag",0);
            cartMapper.update(new Cart() ,cartUpdateWrapper);
        }
    }

    @Override
    public void changeAllSelected(String openId) {
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("open_id",openId);
        cartQueryWrapper.eq("delete_flag",0);
        List<Cart> carts =  cartMapper.selectList(cartQueryWrapper);
        for(Cart cart : carts){
            QueryWrapper<GoodStock> goodStockQueryWrapper = new QueryWrapper<>();
            goodStockQueryWrapper.eq("good_id",cart.getSubGoodId());
            goodStockQueryWrapper.eq("delete_flag",0);
            GoodStock goodStock = goodStockMapper.selectOne(goodStockQueryWrapper);
            if(goodStock != null && goodStock.getStockQuantity() > 0){
                UpdateWrapper<Cart> cartUpdateWrapper = new UpdateWrapper<>();
                cartUpdateWrapper.set("is_selected",0);
                cartUpdateWrapper.set("update_time",new Date());
                cartUpdateWrapper.eq("sub_good_id",cart.getSubGoodId());
                cartUpdateWrapper.eq("open_id",openId);
                cartUpdateWrapper.eq("delete_flag",0);
                cartMapper.update(cart ,cartUpdateWrapper);
            }
        }
    }
}
