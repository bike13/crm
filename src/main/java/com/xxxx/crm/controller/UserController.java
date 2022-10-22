package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import com.xxxx.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     * 登录
     * Controller层(控制层:接收请求、响应结果)
            1.通过形参接收客户端传递的参数
            2.调用业务逻辑层的登录方法,得到登录结果
	        3.响应数据给客户端
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName,userPwd);

        resultInfo.setResult(userModel);
        return resultInfo;
    }

    /**
     修改密码
             1.通过形参接收前端传递的参数(原始密码、新密码、确认密码)
             2.通过request对象，获取设置在cookie中的用户ID
             3.调用Service层修改密码的功能，得到ResultInfo对象
             4.返回ResultInfo对象
     * @param request
     * @param oldpwd
     * @param newpwd
     * @param repeatpwd
     * @return
     */
    @PostMapping("updatepwd")
    @ResponseBody
    public ResultInfo updatepasswoid(HttpServletRequest request,
                                     String oldpwd, String newpwd, String repeatpwd){
        ResultInfo resultInfo = new ResultInfo();

        // 2.通过request对象，获取设置在cookie中的用户ID
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //3.调用Service层修改密码的功能，得到ResultInfo对象
        userService.updatepasswoid(userId,oldpwd,newpwd,repeatpwd);
        resultInfo.setCode(200);
        resultInfo.setMsg("修改成功!");
//        try {
//            // 2.通过request对象，获取设置在cookie中的用户ID
//            int userId = LoginUserUtil.releaseUserIdFromCookie(request);
//            //3.调用Service层修改密码的功能，得到ResultInfo对象
//            userService.updatepasswoid(userId,oldpwd,newpwd,repeatpwd);
//            resultInfo.setCode(200);
//            resultInfo.setMsg("修改成功!");
//        }catch (ParamsException p){
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            //p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("修改失败!");
//        }
        return resultInfo;
    }

    /**
     * 进入修改密码页面
     * data-iframe-tab="${ctx}/user/toPasswordPage"
     * @return
     */
    @GetMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    /**
     * 查询所有的销售
     * @return
     */
    @PostMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }

    /**
     * 分页查询多条
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selecyByParams(UserQuery userQuery){
        System.out.println(userQuery);
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进入用户管理界面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }




    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User User){

        userService.addUser(User);

        return success("用户添加成功!");
    }

    //toAddOrUpdateUserDialog
    //进入添加/修改用户数据页面
    @RequestMapping("toAddOrUpdateUserDialog")
    public String toAddOrUpdateUserDialog(Integer id,HttpServletRequest request){
        //saleChanceId不为空修改 设置到请求域中
        if (id !=null){
            User user = userService.selectByPrimaryKey(id);
            request.setAttribute("userdate",user);
        }
        return "user/add_update";
    }

    /**
     * 用户跟新页面
     * @param User
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User User){

        userService.updateUser(User);

        return success("用户跟新成功!");
    }

    /**
     * 用户模块删除
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUsers(Integer[] ids){

        userService.deleteUsers(ids);

        return success("用户删除成功!");
    }



    /**
     * test专用
     * @return
     */
    @GetMapping("test")
    public String test(){
        return "user/password";
    }



}
