package org.czb.xingcan.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.logging.log4j.util.Strings;
import org.czb.xingcan.db.domain.TestUser;
import org.czb.xingcan.db.service.TestUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class XingcanDbApplicationTests {
    @Autowired
    private TestUserService userService;

    @Test
    void contextLoads() {
        List<TestUser> user = userService.list();
//        System.out.println(user.toString());
    }

    @Test
    void testPage() {
        String address = "";
        LambdaQueryWrapper<TestUser> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(address), TestUser::getAddress, address);
        IPage page = new Page(1, 2);
        userService.page(page, lqw);
    }

}
