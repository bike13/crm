package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RequestMapping("module")
@Controller
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    /**
     * 查询所有的模型
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 取授权页面
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryModuleslist(){
        return moduleService.queryModuleslist();
    }

    /**
     * 进入菜单管理页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "module/module";
    }

    /**
     * 添加模块
     * @return
     */
    @PostMapping("addmodule")
    @ResponseBody
    public ResultInfo addmodule(Module module){
        moduleService.addModule(module);
        return success("模块添加成功");
    }

    /**
     * 修改模块
     * @return
     */
    @PostMapping("updateModule")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("模块修改成功");
    }


    @PostMapping("deleteModule")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("模块删除成功");
    }


    //进入添加资源的页面
    @RequestMapping("addModulePage")
    public String addModulePage(Integer grade,Integer parentId,HttpServletRequest request){
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);
        return "module/add";
    }


    //进入添加资源的页面
    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id,HttpServletRequest request){
        Module module = moduleService.selectByPrimaryKey(id);
        request.setAttribute("module",module);
        return "module/update";
    }


}
