package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;
    //分页查询
    public Map<String,Object> querySaleChancesByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //分页对象
        PageInfo<SaleChance> pageInfo=new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));

        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    /**
         1.参数校验
             customerName客户名称    非空
             linkMan联系人           非空
             linkPhone联系号码       非空，手机号码格式正确
         2.设置相关参数的默认值
             isValid是否有效(0=无效，1=有效)
                设置为有效   1=有效
             createDate创建时间
                 默认是系统当前时间
             updateDate跟新时间
                 默认是系统当前时间

             如果未设置指派人(默认)
                 state分配状态(0=未分配,1=已分配)
                     0=未分配
                 assignTime指派时间  设置为null
                 devResult开发状态(0=未开发，1=开发中，2=开发成功，3=开发失败)
                     0=未开发(默认)
             如果设置了指派人
                 state分配状态(0=未分配,1=已分配)
                     1 =已分配
                 assignTine指派时间
                     系统当前时间
                 devResult开发状态(0=未开发，1=开发中，2=开发成功，3=开发失败)
                     1 =开发中
         3.执行添加操作,判断受影响的行数（自动生成）
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //1.参数校验
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //2.设置相关参数的默认值
        //isValid是否有效(0=无效，1=有效)1=有效
        saleChance.setIsValid(1);
        //createDate创建时间
        saleChance.setCreateDate(new Date());
        //updateDate跟新时间 默认是系统当前时间
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            //未设置
            //state分配状态(0=未分配,1=已分配)0=未分配
            saleChance.setState(StateStatus.UNSTATE.getType());
            //assignTime指派时间  设置为null
            saleChance.setAssignTime(null);
            //devResult开发状态(0=未开发，1=开发中，2=开发成功，3=开发失败)0=未开发(默认)
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }else {
            //已设置
            //state分配状态(0=未分配,1=已分配)1 =已分配
            saleChance.setState(StateStatus.STATED.getType());
            //assignTine指派时间 系统当前时间
            saleChance.setAssignTime(new Date());
            //devResult开发状态(0=未开发，1=开发中，2=开发成功，3=开发失败)1 =开发中
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        //3.执行添加操作,判断受影响的行数（自动生成）
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)<1,"跟新失败");

    }

    /**
     * 更新营销机会
     *     1.参数校验
     *         营销机会ID 非空，数据库中对应的记录存在
     *         customerName客户名称非空
     *         linkMan联系人 非空
     *         linkPhone联系号码 非空，手机号码格式正确
     *     2.设置相关参数的默认值
     *         updateDate更新时间 设置为系统当前时间
     *         assignMan指派人
     *             原始数据未设置
     *                 修改后未设置
     *                     不需要操作
     *                 修改后已设置
     *                     assignTime指派时间 设置为系统当前时间
     *                     分配状态1=已分配
     *                     开发状态1=开发中
     *             原始数据已设置
     *                 修改后未设置
     *                     assignTime指派时问设置为null
     *                     分配状态 0=未分配
     *                     开发状态 0=未开发
     *                 修改后已设置
     *                     判断修改前后是否是同一个指派人
     *                     如果是，设置原时间
     *                     如果不是，则需要更新assignTime指派时间设置为系统当前时间
     *     3.执行更新操作,判断受影响的行数
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updataSaleChance(SaleChance saleChance){
        //营销机会ID 非空，数据库中对应的记录存在customerName客户名称非空
        AssertUtil.isTrue(saleChance.getId()==null,"带跟新记录不存在");
        //原始数据
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"带跟新记录不存在");
        //customerName客户名称非空
        //linkMan联系人 非空
        //linkPhone联系号码 非空，手机号码格式正确
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //updateDate更新时间 设置为系统当前时间
        saleChance.setUpdateDate(new Date());
        //assignMan指派人
        if (StringUtils.isBlank(temp.getAssignMan())){
            //0--
            if (StringUtils.isBlank(saleChance.getAssignMan())){
                //0-->0
                //不需要操作
            }else {
                //0-->1
                //assignTime指派时间 设置为系统当前时间
                saleChance.setAssignTime(new Date());
                //分配状态1=已分配
                saleChance.setState(StateStatus.STATED.getType());
                //开发状态1=开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        }else {
            //1--
            if (StringUtils.isBlank(saleChance.getAssignMan())){
                //1-->0
                //assignTime指派时问设置为null
                saleChance.setAssignTime(null);
                //分配状态 0=未分配
                saleChance.setState(StateStatus.UNSTATE.getType());
                //开发状态 0=未开发
                saleChance.setState(DevResult.UNDEV.getStatus());
            }else {
                //1-->1
                //判断修改前后是否是同一个指派人
                if (temp.getAssignMan().equals(saleChance.getAssignMan())){
                    //如果是，设置原时间
                    saleChance.setAssignTime(temp.getAssignTime());
                }else {
                    //如果不是，则需要更新assignTime指派时间设置为系统当前时间
                    saleChance.setAssignTime(new Date());
                }
            }
        }
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"跟新失败...");

    }

    /**
     * 删除营销机会
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断参数
        AssertUtil.isTrue(ids==null||ids.length<1,"待删除记录不存在");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!=ids.length,"删除失败!");
    }


    //1.参数校验
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        //customerName客户名称    非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空");
        // linkMan联系人           非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        //linkPhone联系号码       非空，手机号码格式正确
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"手机号码不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号码格式不正确");
    }
}
