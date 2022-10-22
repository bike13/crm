package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
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
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /*
     Service层(业务逻辑层:非空判断、条件判断等业务逻辑处理)
         1.参数判断,判断用户姓名、用户密码非空弄
             如果参数为空,抛出异常(异常被控制层捕获并处理)
         2.调用数据访问层，通过用户名查询用户记录，返回用户对象
         3.判断用户对象是否为空
             如果对象为空,抛出异常(异常被控制层捕获并处理)
         4.判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
             如果密码不相等，抛出异常(异常被控制层捕获并处理)
         5.如果密码正确,登录成功
     */
    public UserModel userLogin(String userName, String userPwd){
        //1.参数判断,判断用户姓名、用户密码非空弄
        checkLoginParams(userName,userPwd);
        // 2.调用数据访问层，通过用户名查询用户记录，返回用户对象
        User user = userMapper.queryUserByName(userName);
        //3判断用户对象是否为空
        AssertUtil.isTrue(user==null,"用户不存在");
        //4.判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
        checkUserPwd(userPwd,user.getUserPwd());
        //5.如果密码正确,登录成功  传递user前端回显
        return buildUserInfo(user);
    }

    /**
     service层
         1.接收四个参数(用户ID、原始密码、新密码、确认密码)
         2.通过用户ID查询用户记录，返回用户对象
         3.参数校验(md5加密)
             待更新用户记录是否存在(用户对象是否为空)
             判断原始密码是否为空
             判断原始密码是否正确（查询的用户对象中的用户密码是否原始密码一致)
             判断新密码是否为空
             判断新密码是否与原始密码一致(不允许新密码与原始密码)
             判断确认密码是否为空
             判断确认密码是饿与新密码一致
         4.设置用户的新密码
             需要将新密码通过指定算法进行加密(md5加密)
         5.执行更新操作，判断受影响的行数
         * @param userid
     * @param oldpwd
     * @param newpwd
     * @param repeatpwd
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatepasswoid(Integer userid,String oldpwd,String newpwd,String repeatpwd){
        //2.通过用户ID查询用户记录，返回用户对象
        User user = userMapper.selectByPrimaryKey(userid);
        //3.参数校验(md5加密)
        checkPasswordParams(user,oldpwd,newpwd,repeatpwd);
        //4.设置用户的新密码
        user.setUserPwd(Md5Util.encode(newpwd));
        //5.执行更新操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败！");
    }

    // 3.参数校验(md5加密)
    private void checkPasswordParams(User user, String oldpwd, String newpwd, String repeatpwd) {
        //待更新用户记录是否存在(用户对象是否为空)
        AssertUtil.isTrue(null==user,"待更新用户记录不存在");
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldpwd),"原始密码为空");
        //判断原始密码是否正确（查询的用户对象中的用户密码是否原始密码一致)(md5加密)
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldpwd)),"原始密码不正确");
        //判断新密码是否为空
        AssertUtil.isTrue(null==newpwd||"".equals(newpwd),"新密码为空");
        //判断新密码是否与原始密码一致(不允许新密码与原始密码)
        AssertUtil.isTrue(newpwd.equals(oldpwd),"新密码与原始密码一致");
        //判断确认密码是否为空
        AssertUtil.isTrue(null==repeatpwd||"".equals(repeatpwd),"确认密码为空");
        //判断确认密码是饿与新密码一致
        AssertUtil.isTrue(!repeatpwd.equals(newpwd),"确认密码与新密码不一致");
    }


    //5.如果密码正确,登录成功  传递user前端回显
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    //4.判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
    private void checkUserPwd(String userPwd, String upwd) {
        //加密
        userPwd= Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(upwd),"用户密码错误");
    }

    // 2.调用数据访问层，通过用户名查询用户记录，返回用户对象
    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }

    /**
     * 查询所有的销售
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    //在BaseService中已经定义参数查list方法

    /**
     添加用户
         1.参数校验
             用户名userName 非空，唯一性
             邮箱email      非空
             手机号phone    非空，格式正确
         2.设置参数的默认值
             isvalid     1
             createDate  系统当前时间
             updateDate   系统当前时间
             默认密码     123456->md5加密
         3.执行添加操作，判断受影响的行数
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //1.参数校验
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),null);
        //2置参数的默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //默认密码     123456->md5加密
        user.setUserPwd(Md5Util.encode("123456"));
        //3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加用户失败!");

        /**
         * 用户状态的添加  用户角色中间表
         *  userId用户id
         *  roleIds的添加
         */
        relationuserRole(user.getId(),user.getRoleIds());
    }

    /**
     用户角色关联
         添加操作
             原始角色不存在
             1.不添加新的角色记录
             .  不操作用户角色表
             2.添加新的角色记录
             .  给指定用户绑定相关的角色记录
         更新操作
             原始角色不存在
                 1.不添加新的角色记录
                 .  不操作用户角色表
                 2.添加新的角色记录
                 .  给指定用户绑定相关的角色记录
             原始角色存在
                 1.添加新的角色记录
                 判断已有的角色记录不添加，添加没有的角色记录
                 2.清空所有的角色记录
                 删除用户绑定角色记录
                 3.移除部分角色记录
                 删除不存在的角色记录，存在的角色记录保留
                 4.移除部分角色，添加新的角色―删除不存在的角色记录，存在的角色记录保留，添加新的角色

         如何进行角色分配???
             原有的角色记录不存在
                 给指定用户绑定相关的角色记录
             原有的角色记录存在
                 将用户原有的角色记录删除,添加新的角色记录
         删除操作
             删除指定用户绑定的角色记录

     *
     * @param userId
     * @param roleIds
     */
    private void relationuserRole(Integer userId, String roleIds) {
        //角色id查数量
        Integer count=userRoleMapper.countUserRoleByUserId(userId);
        if (count>0){
            //原有的角色记录存在
            //    将用户原有的角色记录删除,添加新的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)<1,"用户角色状态分配失败");
        }
        //原有的角色记录不存在
        //    给指定用户绑定相关的角色记录
        //批量添加-->设置数据源
        List<UserRole> userRoleList=new ArrayList<>();
        String[] roleIdsAllay=roleIds.split(",");
        for (String roleId:roleIdsAllay){
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(roleId));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            userRoleList.add(userRole);
        }
        //批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=roleIdsAllay.length,"用户角色状态分配失败");

    }

    /**
     修改用户
         1.参数校验
            用户id      非空，存在
             用户名userName 非空，唯一性
             邮箱email      非空
             手机号phone    非空，格式正确
         2.设置参数的默认值
             updateDate   系统当前时间
         3.执行添加操作，判断受影响的行数
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        //1.参数校验
        //用户id      非空，存在
        AssertUtil.isTrue(user.getId()==null || userMapper.selectByPrimaryKey(user.getId())==null
                ,"参数异常请重试");
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        //2置参数的默认值
        user.setUpdateDate(new Date());
        //3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"跟新用户失败!");
        /**
         * 用户状态的添加  用户角色中间表
         *  userId用户id
         *  roleIds的添加
         */
        relationuserRole(user.getId(),user.getRoleIds());

    }


    /**
     * 1.参数校验
     *     用户名userName 非空，唯一性
     *     邮箱email      非空
     *     手机号phone    非空，格式正确
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone,Integer id) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名称不能为空");
        User user = userMapper.queryUserByName(userName);
        //跟新时  要求修改后的用户名不能与自己以外的用户名重复
        if (id!=null){
            AssertUtil.isTrue(user!=null && userName.equals(userMapper.selectByPrimaryKey(id)),"用户名称已重复");
        }else {
            AssertUtil.isTrue(user!=null,"用户名称已重复");
        }
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号不能为空");
    }

    /**
     * 用户模块删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUsers(Integer[] ids) {
        //判断参数
        AssertUtil.isTrue(ids==null||ids.length<1,"请选择要删除的记录");
        //判结果 删除记录条数和待删记录条数一致
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除失败");
        //删除指定用户绑定的角色记录
        for (Integer userId:ids){
            Integer count =userRoleMapper.countUserRoleByUserId(userId);
            if (count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户删除失败!");
            }
        }
    }



}
