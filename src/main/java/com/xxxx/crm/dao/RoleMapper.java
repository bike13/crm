package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {

    //查询角色列表(角色id和角色名字)
    List<Map<String,Object>> queryAllRoles(Integer userId);

    //根据name查
    Role selectByprimaryName(String roleName);
}