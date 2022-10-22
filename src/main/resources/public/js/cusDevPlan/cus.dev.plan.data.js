layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;



    //计划项数据展示
    var  tableIns = table.render({
        //数据表格id
        id : "cusDevPlanListTable",
        elem: '#cusDevPlanList',
        //数据表格自动请求
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,20,30],
        limit : 10,
        //开启头部工具栏
        toolbar: "#toolbarDemo",
        //数据表格行标题
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"},
        ]]
    });


    //头工具栏事件 lay-filter="cusDevPlans"
    table.on('toolbar(cusDevPlans)', function(obj){
        console.log(obj);
        switch(obj.event){
            case "add":
                //添加|修改 计划项 页面
                openAddOrUpdateCusDevPlanDialog();
                break;
            case "success":
                //开发成功--跟新营销机会的开发状态
                updateSaleChanceDevResult(2);
                break;
            case "failed":
                //开发失败
                updateSaleChanceDevResult(3);
                break;
        };
    });


    /**
     * 行监听
     */
    table.on("tool(cusDevPlans)", function(obj){
        console.log(obj);
        var layEvent = obj.event;
        if(layEvent === "edit") {
            //行监听编辑操作
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        }else if(layEvent === "del") {
            //行监听删除操作
            layer.confirm('确定删除当前数据？', {icon: 3, title: "开发计划管理"}, function (index) {
                //post请求传id
                $.post(ctx+"/cus_dev_plan/delete",{ids:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！");

                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            })
        }

    });
    //打开添加三级小窗口
    function openAddOrUpdateCusDevPlanDialog(id){
        var title="添加计划项目页面"
        var url=ctx+"/cus_dev_plan/toAddOrUpdateCusDevPlanDialog?sId="+$("[name='id']").val();

        //表示修改
        if (id!=null && id!=''){
            title="跟新计划项目页面"
            url= url+"&id="+id;
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["500px","300px"],
            //最大最小化
            maxmin:true,
            content : url
        });
    }


    //跟新营销机会的开发状态
    function updateSaleChanceDevResult(devResult) {
        layer.confirm('确定执行当前操作？', {icon: 3, title: "计划项维护"}, function (index) {
            //营销机会id
            var sid=$("[name='id']").val();
            $.post(ctx + "/cus_dev_plan/updateSaleChanceDevResult",
                {
                    id: sid,
                    //修改状态
                    devResult: devResult
                }, function (data) {
                    if (data.code == 200) {
                        layer.msg("跟新成功！",{icon: 6});
                        //关闭窗口
                        layer.closeAll("iframe");
                        //刷新父页面
                        parent.location.reload();
                    } else {
                        layer.close(index)
                        layer.msg(data.msg, {icon: 5});
                    }
            });
        })
    }


});
