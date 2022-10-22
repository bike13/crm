package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;


    @Resource
    private RoleMapper roleMapper;

    /**
     * 查询角色列表
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 角色的添加
     *  判断参数
     *      用户名不能为空
     *      唯一
     *   默认值
     *      创建时间
     *      跟新时间
     *  判断结果
     *      返回行数
     * @param roleQuery
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(RoleQuery roleQuery) {
        Role role=new Role();
        //判断参数
        AssertUtil.isTrue(StringUtils.isBlank(roleQuery.getRoleName()),"角色的姓名不能为空");
        AssertUtil.isTrue(roleMapper.selectByprimaryName(roleQuery.getRoleName())!=null,"角色已存在");
        //默认值
        //创建时间//跟新时间
        role.setRoleName(roleQuery.getRoleName());
        role.setRoleRemark(roleQuery.getRoleRemark());
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1,"用户添加失败");

    }

    /**
     * 角色的修改
     *  判断参数
     *      id  判断空 存在
     *      用户名不能为空 唯一
     *   默认值
     *      跟新时间
     *  判断结果
     *      返回行数
     * @param roleQuery
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(RoleQuery roleQuery) {
        //判断参数
        AssertUtil.isTrue(roleQuery.getId()==null,"修改参数异常");
        Role role = roleMapper.selectByPrimaryKey(roleQuery.getId());
        AssertUtil.isTrue(roleQuery.getId()==null||role==null,"修改参数异常");

        AssertUtil.isTrue(StringUtils.isBlank(roleQuery.getRoleName()),"角色的姓名不能为空");
        //判断存在    角色用户名查询存在   查询对象的id不等于传递的id
        AssertUtil.isTrue(roleMapper.selectByprimaryName(roleQuery.getRoleName())!=null
                &&(!roleMapper.selectByprimaryName(roleQuery.getRoleName()).getId().equals(roleQuery.getId())),"角色已存在");

        //默认值
        role.setRoleName(roleQuery.getRoleName());
        role.setRoleRemark(roleQuery.getRoleRemark());
        role.setUpdateDate(new Date());
        //返回行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色更新失败!");
    }

    /**
     * 删除角色
         * 判断参数
         * 判断结果
     * @param roleid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleid) {
        //判断参数
        AssertUtil.isTrue(roleid==null||roleMapper.selectByPrimaryKey(roleid)==null,"带删除记录不存在");
        //判断结果
        AssertUtil.isTrue(roleMapper.deleteByPrimaryKey(roleid)!=1,"角色删除失败");

    }


    /**
     角色授权
     将对应的角色ID与资源ID，添加到对应的权限表中
     直接添加权限:不合适，会出现重复的权限数据（执行修改权限操作后删除权限操作时)
     推荐使用:
     先将已有的权限记录删除，再将需要设置的权限记录添加
     1.通过角色ID查询对应的权限记录
     2.如果权限记录存在，则删除对应的角色拥有的权限记录
     3.如果有权限记录，则添加权限记录
     *
     * @param mids
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mids, Integer roleId) {
        //角色id为空 或角色id对应的角色为空 则报错
        AssertUtil.isTrue(roleId==null||roleMapper.selectByPrimaryKey(roleId)==null,"角色授权数据异常!");
        //根据 角色id删除操作
        if(permissionMapper.selectByPrimaryByRoleId(roleId)!=null){
            permissionMapper.deleteByPrimaryByRoleId(roleId);
        }
        //3.如果有权限记录，则添加权限记录
        if (mids!=null && mids.length>0){
            List<Permission>  permissionList=new ArrayList<>();
            for (Integer id:mids){
                Permission permission = new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(id);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setAclValue(moduleMapper.selectByPrimaryKey(id).getOptValue());
                permissionList.add(permission);
            }
            permissionMapper.insertBatch(permissionList);
        }
    }

}
