package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

//客户开发计划管理
public class CusDevPlanQuery extends BaseQuery {
    //营销计划
    private Integer saleChanceId;

    public Integer getSaleChanceId() {
        return saleChanceId;
    }

    public void setSaleChanceId(Integer saleChanceId) {
        this.saleChanceId = saleChanceId;
    }
}
