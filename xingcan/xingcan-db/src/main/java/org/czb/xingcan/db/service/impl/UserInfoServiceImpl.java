package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.OrderMapper;
import org.czb.xingcan.db.dao.UserAddressMapper;
import org.czb.xingcan.db.dao.UserInfoMapper;
import org.czb.xingcan.db.domain.Order;
import org.czb.xingcan.db.domain.UserAddress;
import org.czb.xingcan.db.domain.UserInfo;
import org.czb.xingcan.db.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAddressMapper userAddressMapper;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public List<UserAddress> queryUserAddressList(QueryWrapper<UserAddress> qw) {
        return userAddressMapper.selectList(qw);
    }

    @Override
    public void saveOrUpdateAddress(String openId, UserAddress userAddress) {
        if(0 == userAddress.getIsDefault()){
            UpdateWrapper<UserAddress> uw = new UpdateWrapper<>();
            uw.set("is_default",1);
            uw.eq("open_id",openId);
            userAddressMapper.update(null,uw);
        }
        if(userAddress.getId()!=null){
            UpdateWrapper<UserAddress> uw = new UpdateWrapper<>();
            uw.set("update_time", new Date());
            uw.eq("id",userAddress.getId());
            userAddressMapper.update(userAddress,uw);
        }else{
            userAddress.setOpenId(openId);
            userAddress.setAddTime(new Date());
            userAddressMapper.insert(userAddress);
        }
    }

    @Override
    public void deleteAddress(UserAddress userAddress) {
        userAddressMapper.deleteById(userAddress.getId());
    }

    @Override
    public Long queryUserOrderCount(QueryWrapper<Order> qw) {
        return orderMapper.selectCount(qw);
    }
}
