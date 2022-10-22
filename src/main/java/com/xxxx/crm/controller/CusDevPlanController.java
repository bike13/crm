package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private CusDevPlanService cusDevPlanService;

    @Resource
    private SaleChanceService saleChanceService;

    //进入客户开发计划
    @RequestMapping("index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }

    //打开控制开发页面
    @RequestMapping("toCusDevPlanPage")
    public String toCusDevPlanPage(Integer id, HttpServletRequest request){
        //详情页面
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        request.setAttribute("saleChance",saleChance);

        return "cusDevPlan/cus_dev_plan_data";
    }


    //查询 客户开发计划-记录
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){

        return cusDevPlanService.queryCusDevPlanByParams(cusDevPlanQuery);
    }

    /**
     * 添加计划项目
     * @param cusDevPlan
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项添加成功!");
    }

    /**
     * 跟新计划项目
     * @param cusDevPlan
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项跟新成功!");
    }

    /**
     * 删除计划项
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer[] ids){
        cusDevPlanService.deleteSaleChance(ids);
        return success("营销机会删除成功!");
    }

    /**
     * 客户开发计划---开发状态
     * @param id
     * @param devResult
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        cusDevPlanService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态修改成功!");
    }

    //进入客户开发计划-添加(三级小窗口)
    @RequestMapping("toAddOrUpdateCusDevPlanDialog")
    public String toAddOrUpdateCusDevPlanDialog(Integer sId,Integer id,HttpServletRequest request){
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        request.setAttribute("cusDevPlan",cusDevPlan);
        request.setAttribute("sId",sId);
        return "cusDevPlan/add_update";
    }

}
