package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    //id查Permission
    Integer selectByPrimaryByRoleId(Integer roleId);

    //roleId删 Permission
    void deleteByPrimaryByRoleId(Integer roleId);

    //通过角色id,查对应的资源id
    List<Integer> queryRoleHasmoduleidsByRoleId(Integer roleId);

    //根据userid查Permission
    List<String> selectUserHasPermissionByUserId(Integer userId);

    //通过资源id查所有
    Integer selectByPrimaryByModuleId(Integer moduleId);

    //删除所有对应资源id
    Integer deleteByPrimaryByModuleId(Integer id);
}