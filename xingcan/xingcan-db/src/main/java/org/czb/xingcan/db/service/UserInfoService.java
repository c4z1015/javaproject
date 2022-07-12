package org.czb.xingcan.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.czb.xingcan.db.domain.Order;
import org.czb.xingcan.db.domain.UserAddress;
import org.czb.xingcan.db.domain.UserInfo;

import java.util.List;

public interface UserInfoService extends IService<UserInfo> {
    List<UserAddress> queryUserAddressList(QueryWrapper<UserAddress> qw);

    void saveOrUpdateAddress(String openId, UserAddress userAddress);

    void deleteAddress(UserAddress userAddress);

    Long queryUserOrderCount(QueryWrapper<Order> qw);
}
