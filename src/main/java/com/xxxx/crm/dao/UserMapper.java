package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    //Dao层（数据访问层:数据库中增删改查操作)
    //通过用户名查询用户记录，返回用户对象
    User queryUserByName(String userName);

    //已有单条记录跟新

    //查询所以的销售人员
    List<Map<String,Object>> queryAllSales();

    //根据对象查询所有是指令已继承 selectByParams

    //跟新单条记录已自动生成  updateByPrimaryKeySelective

    //用户模块删除多条记录已继承 deleteBatch(ids)
}