package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.TestUserMapper;
import org.czb.xingcan.db.domain.TestUser;
import org.czb.xingcan.db.service.TestUserService;
import org.springframework.stereotype.Service;

@Service
public class TestUserServiceImpl extends ServiceImpl<TestUserMapper, TestUser> implements TestUserService {

}
