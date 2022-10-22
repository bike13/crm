layui.use(['table','layer',"form"],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

       //用户列表展示
        var  tableIns = table.render({
            //表id
            id : "userListTable",
            elem: '#userList',
            url : ctx+'/user/list',
            cellMinWidth : 95,
            page : true,
            height : "full-125",
            limits : [10,20,30],
            limit : 10,
            //开启头监控
            toolbar: "#toolbarDemo",
            cols : [[
                //选择框
                {type: "checkbox", fixed:"left", width:50},

                {field: "id", title:'编号',fixed:"true", width:80},
                {field: 'userName', title: '用户名', minWidth:50, align:"center"},
                {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
                {field: 'phone', title: '手机号', minWidth:100, align:'center'},
                {field: 'trueName', title: '真实姓名', align:'center'},
                {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
                {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
                //开启行监控
                {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
            ]]
        });

    //多条件查询 类选择器
    $(".search_btn").click(function (){
        // 重载 - 这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName: $("[name='userName']").val()  // 用户名
                ,email: $("[name='email']").val() // 邮箱
                ,phone: $("[name='phone']").val() // 手机号
              }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    })


    //头工具栏事件  添加/删除'toolbar(数据表格容器)'
    table.on('toolbar(users)', function(obj){
        switch(obj.event){
            case "add":
                openAddOrUpdateUserDialog();
                break;
            case "del":
                delUser();
                break;
        };
    });

    //添加用户操作
    function openAddOrUpdateUserDialog(id){
        var title="添加用户页面"
        var url=ctx+"/user/toAddOrUpdateUserDialog";

        //表示修改
        if (id!=null && id!=''){
            title="跟新用户页面"
            url= url+"?id="+id;
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["650px","400px"],
            //最大最小化
            maxmin:true,
            content : url
        });
    }


    //head-删除操作窗口
    function delUser(){
        //获取选中框  根据数据表格id获取
        var checkStatus = table.checkStatus("userListTable");
        var saleCheckData =checkStatus.data;
        //判断
        if (saleCheckData.length<1){
            //没有选中
            layer.msg("请选择要删除的记录!",{icon:5});
            return ;
        }
        layer.confirm('确定删除该选中的记录吗?',{icon:3,title:"用户管理"},function (index){
            //关闭询问框
            layer.close(index);
            //提取数据
            var ids="ids=";

            for (var i=0;i <saleCheckData.length;i++){
                if (i<saleCheckData.length-1){
                    ids = ids + saleCheckData[i].id+"ids=";
                }else {
                    ids = ids + saleCheckData[i].id;
                }
            }

            //请求后台
            $.ajax({
                type: "post",
                url: ctx+"/user/delete",
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

    /**
     * 行监听
     */
    table.on("tool(users)", function(obj){
        console.log(obj);
        var layEvent = obj.event;
        if(layEvent === "edit") {
            //行监听编辑操作
            openAddOrUpdateUserDialog(obj.data.id);

        }else if(layEvent === "del") {
            //行监听删除操作
            layer.confirm('确定删除当前数据？', {icon: 3, title: "用户管理"}, function (index) {
                //post请求传id
                $.post(ctx+"/user/delete",{ids:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！");
                        //关闭窗口
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            })
        }

    });

});
