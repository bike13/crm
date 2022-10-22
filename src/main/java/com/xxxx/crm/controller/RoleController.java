package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    //获取所有的角色
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    //角色表多条件查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    //data-tab="role/index" 进入角色管理页面
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    //ctx+"/role/toSaleChanePage";进入角色添加页面
    @RequestMapping("toSaleChanePage")
    public String toSaleChanePage(Integer id, HttpServletRequest request){
        //如果存在id表示修改
        if (id!=null){
            Role role = roleService.selectByPrimaryKey(id);
            request.setAttribute("role",role);
        }
        return "role/add_update";
    }

    //角色添加
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addRole(RoleQuery roleQuery){
        roleService.addRole(roleQuery);
        return success("角色添加成功!");
    }

    //角色跟新
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(RoleQuery roleQuery){
        roleService.updateRole(roleQuery);
        return success("角色跟新成功!");
    }


    //角色跟新
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleid){
        roleService.deleteRole(roleid);
        return success("角色删除成功!");
    }



    /**
     * 角色授权
     * @param mids
     * @param roleId
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids, Integer roleId){

        roleService.addGrant(mids,roleId);
        return success("角色授权成功");
    }


}
