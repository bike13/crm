layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;



    //第一个实例
    var tableIns= table.render({
        id:'saleChanceTable'
        ,elem: '#saleChanceList'
        ,height: "full-125"
        ,cellMinWidth:95
        ,url: ctx+'/sale_chance/list?flag=1' //数据接口
        ,page: true //开启分页
        ,limit:10
        ,limits:[10,20,30]
        //开启头部工具栏
        ,toolbar: '#toolbarDemo'
        ,cols: [[ //表头
            //sort: true有排序箭头
            //复选框
            {type: "checkbox", fixed:"center"},
            {field: 'id', title: '编号', sort: true, fixed: 'left'},
            {field: 'chanceSource', title: '机会来源',align:'center'},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'cent er'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'updateDate', title: '跟新时间', align:'center'},
            {field: 'devResult', title: '开发状态', align:'center',templet: function (d){
                    return formatDevResult(d.devResult);//返回状态
                }},
            //绑定行工具栏
            {title: '操作', templet:'#op',fixed:"right",align:"center", minWidth:150}
        ]]
    })
    /**
     * 0未开发 1开发中 2开发成功 3开发失败 4未知
     * @param devResult
     * @returns {string}
     */
    function formatDevResult(devResult){
        if (devResult==0){
            return "<div style='color:yellow '>未开发</div>";
        }else if(devResult==1){
            return "<div style='color: orange'>开发中</div>";
        }else if(devResult==2){
            return "<div style='color: green'>开发成功</div>";
        }else if(devResult==3){
            return "<div style='color: red'>开发失败</div>";
        }else {
            return "<div style='color: blue'>未知</div>";
        }
    }

    $(".search_btn").click(function (){
        // 重载 - 这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("[name='customerName']").val()  // 客户名
                ,createMan: $("[name='createMan']").val() // 创建人
                ,devResult: $("#devResult").val() // 分配状态
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    })


    //行监听  tool(saleChances) tool行  lay-filter="saleChances"容器id
    table.on('tool(saleChances)', function(obj){
        console.log(obj);
        var layEvent = obj.event;
        if(layEvent === "dev") {
            //开发操作
            openCusDevPlanDialog("计划项数据开发",obj.data.id);

        }else if(layEvent === "info") {
            //详情操作
            openCusDevPlanDialog("计划项数据维护",obj.data.id);
        }
    });

    //开发或详情页面
    function openCusDevPlanDialog(title,id){
        //打开窗口
        layui.layer.open({
            title : title,
            type : 2,
            area:["750px","550px"],
            //最大最小化
            maxmin:true,
            content : ctx+"/cus_dev_plan/toCusDevPlanPage?id="+id
        });
    }


});
