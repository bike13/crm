package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

//角色查询
public class RoleQuery extends BaseQuery {

    private Integer id;

    private String roleName;

    private String roleRemark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleRemark() {
        return roleRemark;
    }

    public void setRoleRemark(String roleRemark) {
        this.roleRemark = roleRemark;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
