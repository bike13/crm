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
        ,url: ctx+'/sale_chance/list' //数据接口
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
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'uname', title: '指派人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'updateDate', title: '跟新时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet: function (d){
                return formatState(d.state);//返回状态
            }},
            {field: 'devResult', title: '开发状态', align:'center',templet: function (d){
                return formatDevResult(d.devResult);//返回状态
            }},
            //绑定行工具栏
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    })
    /**
     * 0 未 1已 其他 未知
     * @param state
     */
    function formatState(state){
        if (state==0){
            return "<div style='color:yellow '>未分配</div>";
        }else if(state==1){
            return "<div style='color: green'>已分配</div>";
        }else{
            return "<div style='color: red'>未知</div>";
        }
    }

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

    //多条件查询
    $(".search_btn").click(function (){
        // 重载 - 这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("[name='customerName']").val()  // 客户名
                ,createMan: $("[name='createMan']").val() // 创建人
                ,state: $("#state").val() // 分配状态
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    })


    //头工具栏事件  添加/删除
    table.on('toolbar(saleChances)', function(obj){
        console.log(obj);
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case "add":
                openAddOrUpdateSaleChanceDialog();
                break;
            case "del":
                delSaleChance(checkStatus.data);
                break;
        };
    });

    //打开弹出层窗口
    function openAddOrUpdateSaleChanceDialog(saleChanceId){
        var url  =  ctx+"/sale_chance/toSaleChanePage";
        var title="<h2>营销机会管理-机会添加</h2>";
        //saleChanceId==null添加,e修改
        if (saleChanceId!=null && saleChanceId!=""){
            title="<h2>营销机会管理-机会跟新</h2>";
            url = url+"?saleChanceId="+saleChanceId;
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["500px","620px"],
            //最大最小化
            maxmin:true,
            content : url
        });
    }


    //head-删除操作窗口
    function delSaleChance(datas){
        //获取选中框
        var checkStatus = table.checkStatus("saleChanceTable");
        console.log(checkStatus);
        var saleCheckData =checkStatus.data;
        //判断
        if (saleCheckData.length<1){
            //没有选中
            layer.msg("请选择要删除的记录!",{icon:5});
            return ;
        }
        layer.confirm('确定删除该选中的记录吗?',{icon:3,title:"营销机会管理"},function (index){
            //关闭询问框
            layer.close(index);
            //提取数据
            var ids="ids=";
            for (var i=0;i <saleCheckData.length;i++){
                if (i<saleCheckData.length-1){
                    ids = ids + saleCheckData[i].id+"&ids=";
                }else {
                    ids = ids + saleCheckData[i].id;
                }
            }
            //请求后台
            $.ajax({
                type: "post",
                url: ctx+"/sale_chance/delete",
                data: ids,
                success:function (result){
                    //成功|失败提示
                    if (result.code==200){
                        layer.msg("删除成功!",{icon:6});
                        //刷新表单 //第一个实例
                        tableIns.reload();
                    }else {
                        layer.msg(result.msg,{icon:5});
                    }
                }
            })
        });
    }
    //行监听  tool(saleChances) tool行  lay-filter="saleChances"容器id
    table.on('tool(saleChances)', function(obj){
        console.log(obj);
        var layEvent = obj.event;
        if(layEvent === "edit") {
            //跟新操作
            openAddOrUpdateSaleChanceDialog(obj.data.id);

        }else if(layEvent === "del") {
            //行删除操作
            //layui.confirm('确定删除该记录吗?',{icon:3,title:"营销机会管理"},function (index){
            layer.confirm('确定删除该记录吗?',{icon:3},function (index){
                //确定删除,关闭
                layer.close(index);
                //请求后台
                $.ajax({
                    type: "post",
                    url: ctx+"/sale_chance/delete",
                    data:{
                        ids: obj.data.id
                    },
                    success:function (result){
                        //成功|失败提示
                        if (result.code==200){
                            layer.msg("删除成功!",{icon:6});
                            //刷新表单 //第一个实例
                            tableIns.reload();
                        }else {
                            layer.msg(result.msg,{icon:5});
                        }
                    }
                })
            })
        }
    });
});
