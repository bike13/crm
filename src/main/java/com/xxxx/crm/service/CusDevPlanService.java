package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
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
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    //分页查询
    public Map<String,Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(cusDevPlanQuery.getPage(),cusDevPlanQuery.getLimit());
        //分页对象  返回的对象集合
        PageInfo<CusDevPlan> pageInfo=new PageInfo<>(cusDevPlanMapper.selectByParams(cusDevPlanQuery));

        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    /**
     * 客户开发计划(三级)----添加营销计划
     * 添加客户开发计划项数据
     *     1.参数校验
     *         营销机会ID  非空，数据存在
     *         计划项内容   非空
     *         计划时间    非空
     *     2.设置参数的默认值
     *         是否有效    默认有效
     *         创建时间    系统当前时间
     *         修改时间    系统当前时间
     *     3.执行添加操作，判断受影响的行数
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        //1.参数校验
        checkCusDevPlanParams(cusDevPlan);
        //2.设置参数的默认值
        //是否有效    默认有效
        cusDevPlan.setIsValid(1);
        //创建时间    系统当前时间
        cusDevPlan.setCreateDate(new Date());
        //修改时间    系统当前时间
        cusDevPlan.setUpdateDate(new Date());
        // 3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)<1,"项目数据添加失败");
    }

    /**
     * 客户开发计划(三级)---编辑窗口
     * 1.参数校验
     *        计划项ID  非空，数据存在
     *       营销机会ID  非空，数据存在
     *       计划项内容   非空
     *       计划时间    非空
     * 2.设置参数的默认值
     *     修改时间    系统当前时间
     * 3.执行跟新操作，判断受影响的行数
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //1.参数校验
        //计划项ID  非空，数据存在
        AssertUtil.isTrue(cusDevPlan.getId()==null ||
                cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"数据异常,请重试!");
        checkCusDevPlanParams(cusDevPlan);
        //2.设置参数的默认值
        //修改时间    系统当前时间
        cusDevPlan.setUpdateDate(new Date());
        // 3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)<1,"项目数据添加失败");
    }


    /**
     * 删除计划项目
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断参数
        AssertUtil.isTrue(ids==null||ids.length<1,"待删除记录不存在");
        AssertUtil.isTrue(cusDevPlanMapper.deleteBatch(ids)!=ids.length,"删除失败!");
    }

    /**
     * 1.参数校验
     *     营销机会ID  非空，数据存在
     *     计划项内容   非空
     *     计划时间    非空
     * @param cusDevPlan
     */
    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        Integer saleChanceId = cusDevPlan.getSaleChanceId();
        //营销机会ID  非空，数据存在
        AssertUtil.isTrue(saleChanceId==null||saleChanceMapper.selectByPrimaryKey(saleChanceId)==null
        ,"数据异常请重试");
        //计划项内容   非空
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),"计划项内容不能为空");
        //计划时间    非空
        AssertUtil.isTrue(cusDevPlan.getPlanDate()==null,"计划时间不能为空");
    }

    /**
     * 修改开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //判断参数
        AssertUtil.isTrue(id==null ,"带跟新记录不存在!");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(saleChance==null ,"带跟新记录不存在!");
        saleChance.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"开发状态跟新失败");

    }
}
