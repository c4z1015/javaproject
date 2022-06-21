package org.czb.xingcan.db.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.czb.xingcan.db.domain.TestUser;

@Mapper
public interface TestUserMapper extends BaseMapper<TestUser> {
//    @Select("select * from t_test where id = #{id}")
//    TestUser selectByPrimaryKey(Integer id);
}