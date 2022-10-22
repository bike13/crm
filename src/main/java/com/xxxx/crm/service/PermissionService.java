package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {
    @Resource
    private PermissionMapper permissionMapper;

    //根据userid查Permission
    public List<String> selectUserHasPermissionByUserId(Integer userId) {
        return permissionMapper.selectUserHasPermissionByUserId(userId);

    }

}
