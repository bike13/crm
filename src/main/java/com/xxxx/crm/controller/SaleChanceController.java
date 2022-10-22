package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.utils.Power;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    //BaseController中ctx根路径
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private UserService userService;

    /**
     * 数据表格查询 营销机会数据表格 开发计划数据表格
     * flag=1表示 开发计划数据表格 （已分配的指派当前用户id的数据）
     * @param saleChanceQuery
     * @return
     */
    @Power(code = "101001")
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChancesByParams(SaleChanceQuery saleChanceQuery
            ,Integer flag,HttpServletRequest request){
        if (flag!=null&& flag==1){
            //开发计划数据表格
            //设置分配状态 1=已分配的进行查询
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置当前用户为指派人
            int userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        return saleChanceService.querySaleChancesByParams(saleChanceQuery);
    }

    /**
     * 进入营销机会管理页面
     * //index
     * @return
     */
    @Power(code = "1010")
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销集合
     * @return
     */
    @Power(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        //String userName = CookieUtil.getCookieValue(request, "userName");

        //createMan创建人
        //saleChance.setCreateMan(userName);


        //从cookie中获取userId    LoginUserUtil.releaseUserIdFromCookie(request)
        //userService.selectByPrimaryKey     根据id 查询详情 User
        //User.getUserName()   获取昵称
        saleChance.setCreateMan(userService.selectByPrimaryKey(LoginUserUtil.releaseUserIdFromCookie(request)).getUserName());

        saleChanceService.addSaleChance(saleChance);

        return success("营销机会添加成功!");
    }

    /**
     * 跟新营销集合
     * @return
     */
    @Power(code = "101004")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance, HttpServletRequest request){

        saleChanceService.updataSaleChance(saleChance);

        return success("营销机会跟新成功!");
    }

    //进入添加/修改营销机会数据页面
    @Power(code = "1010")
    @RequestMapping("toSaleChanePage")
    public String toSaleChanePage(Integer saleChanceId,HttpServletRequest request){
        //saleChanceId不为空修改 设置到请求域中
        if (saleChanceId !=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            request.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 删除营销集合
     * @return
     */
    @Power(code = "101003")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会删除成功!");
    }

    //@Power(code = "1010")
    @RequestMapping("test")
    public String test(){
        System.out.println(122);
        return "cus_dev_plan/index";
    }
}
