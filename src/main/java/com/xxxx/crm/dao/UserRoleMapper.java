package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    //y用户id查返回数量
    Integer countUserRoleByUserId(Integer userId);
    //删除用户id,角色状态
    Integer deleteUserRoleByUserId(Integer userId);
}