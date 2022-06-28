package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.UserInfoMapper;
import org.czb.xingcan.db.domain.UserInfo;
import org.czb.xingcan.db.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
