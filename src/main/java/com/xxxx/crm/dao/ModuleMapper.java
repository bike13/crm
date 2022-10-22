package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ModuleMapper extends BaseMapper<Module,Integer> {
    //查询所有的模型mode
    List<TreeModel> queryAllModules();

    //查询所有的资源集合
    List<Module> queryModuleslist();

    //根据层级和模型名称查  当多个参数时
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName")String moduleName);
    //根据层级和URl查
    Module queryModuleByGradeAndURL(@Param("grade")Integer grade,@Param("url")String url);

    //根据权限码查
    Module queryModuleByOptValue(String optValue);

    //根据父级id查
    Integer queryModuleByParentId(Integer parentId);
}