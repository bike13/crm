package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import com.xxxx.crm.vo.Permission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有模型
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId){
        //获取所有资源的一个列表
        List<TreeModel> treeModels = moduleMapper.queryAllModules();

        //通过角色id,查对应的资源id
        List<Integer> permissionIds =permissionMapper.queryRoleHasmoduleidsByRoleId(roleId);
        //遍历资源id与用户的所有资源匹配,相同则设置选中
        if (permissionIds!=null && permissionIds.size()>0){
            treeModels.forEach(treeModel -> {
                if (permissionIds.contains(treeModel.getId())){
                    treeModel.setChecked(true);
                }
            });
        }

        return treeModels;

    }

    /**
     * 查询所有的资源
     * @return
     */
    public Map<String,Object> queryModuleslist(){
        Map<String,Object> map = new HashMap<>();
        List<Module> modulelist = moduleMapper.queryModuleslist();
        map.put("count",modulelist.size());
        map.put("data",modulelist);
        map.put("code",0);
        map.put("msg","");
        return map;
    }

    /**
     * 添加模块
     * 1.参数校验
     *     模块名称 moduleName
     *         非空，同一层级下模块名称唯一
     *     地址url
     *         二级菜单(grade=1)，非空且不可重复
     *     父级菜单 parentId
     *         —级菜单(目录grade=O) null
     *         二级|三级菜单(菜单|按钮 grade=1或2)    非空,父级菜单必须存在
     *     层级 grade
     *         非空,0|1|2
     *     权限码optValue
     *         非空，不可重复
     * 2.设置参数的默认值
     *     是否有效isValid  1
     *     创建时间createDate 系统当前时间
     *     修改时间updateDate 系统当前时间
     * 3.执行添加操作，判断受影响的行数
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module){
        //模块名称moduleName
        //层级 grade
        //    非空,0|1|2
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null==grade||!(grade==1||grade==2||grade==0),"层级名称不合法");
        //    非空，同一层级下模块名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName()),"该层级下模块名称已存在");
        //地址url
        //    二级菜单(grade=1)，非空且不可重复
        if (grade==1){
            // 地址url 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"URL地址不能为空");
            // 地址url 不可重复
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndURL(grade,module.getUrl()),"URL地址不能重复");
        }
        //父级菜单 parentId
        if (grade==0){
            //    —级菜单(目录grade=O)  -1
            module.setParentId(-1);
            //AssertUtil.isTrue(null==module.getParentId(),"父级菜单必须为空");
        }else {
            //    二级|三级菜单(菜单|按钮 grade=1或2)    非空,父级菜单必须存在
            AssertUtil.isTrue(null==module.getParentId(),"父级菜单不能为空");
            AssertUtil.isTrue(null==moduleMapper.selectByPrimaryKey(module.getParentId()),"父级菜单必须存在");
        }
        //权限码 optValue
        //    非空，不可重复
        AssertUtil.isTrue(null==module.getOptValue(),"权限码不能为空");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码不可重复");
        //2.设置参数的默认值
        //    是否有效isValid  1
        module.setIsValid((byte) 1);
        //    创建时间createDate 系统当前时间
        module.setCreateDate(new Date());
        //    修改时间updateDate 系统当前时间
        module.setUpdateDate(new Date());

        AssertUtil.isTrue(moduleMapper.insertSelective(module)!=1,"模块添加失败");
    }

    /**
    修改模块
         1.参数校验
             id  非空，数据存在
             层级grade    非空0|1|2
             模块名称 moduleName 非空，同一层级下模块名称唯一玉不包含当前修改记录本身
             地址url   二级菜单(grade=1)，非空且同一层级下不可重复（不包含当前修改记录本身)
             权限码optValue  非空，不可重复（不包含当前修改记录本身)
         2.设置参数的默认值
             修改时间updateDate系统当前时间
         3.执行更新操作，判断受影响的行数
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        //id  非空，数据存在
        AssertUtil.isTrue(null==module.getId(),"待跟新记录不存在!");
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(null==temp,"待跟新记录不存在!");
        //层级 grade
        //    非空,0|1|2
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null==grade||!(grade==1||grade==2||grade==0),"层级名称不合法");

        //模块名称 moduleName 非空，同一层级下模块名称唯一 且不包含当前修改记录本身)
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        //根据名称查模块对象
        temp=moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if (temp!=null){
            AssertUtil.isTrue(!module.getId().equals(temp.getId()),"该层级下模块名称已存在");
        }
        //地址url
        //二级菜单(grade=1)，非空且同一层级下不可重复（不包含当前修改记录本身)
        if (grade==1){
            // 地址url 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"URL地址不能为空");
            temp=moduleMapper.queryModuleByGradeAndURL(grade,module.getUrl());
            if (temp!=null){
                // 地址url 不可重复
                AssertUtil.isTrue(!module.getId().equals(temp.getId()),"URL地址不能重复");
            }
        }
        //权限码 optValue
        //非空，不可重复（不包含当前修改记录本身)
        AssertUtil.isTrue(null==module.getOptValue(),"权限码不能为空");
        temp=moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (temp!=null){
            AssertUtil.isTrue(!module.getId().equals(temp.getId()),"权限码不可重复");
        }
        //2.设置参数的默认值
        //    修改时间updateDate 系统当前时间
        module.setUpdateDate(new Date());

        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)!=1,"模块添加失败");
    }

    /**
     删除资源
         1.判断删除的记录是否存在
         2.如果当前资源存在子记录，则不可删除
         3.删除资源时，将对应的权限表的记录也删除（判断权限表中是否存在关联数据，如果存在，则删除)
         4.执行删除(更新)操作，判断受影响的行数
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        //1.判断删除的记录是否存在
        AssertUtil.isTrue(null==id,"待删除记录不存在!");
        Module temp = moduleMapper.selectByPrimaryKey(id);

        AssertUtil.isTrue(null==temp,"待删除记录不存在!");
        //2.如果当前资源存在子记录，则不可删除
        Integer count=moduleMapper.queryModuleByParentId(id);
        AssertUtil.isTrue(count>0,"删除记录存在子记录,不能删除!");
        //3.删除资源时，将对应的权限表的记录也删除（判断权限表中是否存在关联数据，如果存在，则删除)
        count=permissionMapper.selectByPrimaryByModuleId(id);
        if (count>0){
            permissionMapper.deleteByPrimaryByModuleId(id);
        }
        //4.执行删除(更新)操作，判断受影响的行数
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)<1,"删除失败资源!");
    }
}
