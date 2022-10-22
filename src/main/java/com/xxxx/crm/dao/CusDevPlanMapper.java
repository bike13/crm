package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.CusDevPlan;

public interface CusDevPlanMapper extends BaseMapper<CusDevPlan,Integer> {

    //继承通过id查客户开发计划的方法selectByParams

    //继承单条数据跟新操作已存在 insertSelective

    //删除开发计划已继承deleteBatch
}