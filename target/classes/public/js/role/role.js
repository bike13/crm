layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

       //数据表格
    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        //头工具栏id
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            //行工具栏id
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });


    //多条件查询  类选择器
    $(".search_btn").click(function (){
        // 重载 - 这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                roleName: $("[name='roleName']").val()  // 客户名
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    })

    //头工具栏事件  添加/删除
    table.on('toolbar(roles)', function(obj){
        console.log(obj);
        switch(obj.event){
            case "add":
                //添加
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                //授权
                //表格多选的id数据
                openAddGrantDialog(table.checkStatus(obj.config.id).data);
                break;
        };
    });

    function openAddGrantDialog(datas) {
        if(datas.length==0){
            layer.msg("请选择待授权的角色记录!",{icon:5});
            return;
        }
        if(datas.length>1){
            layer.msg("暂不支持批量角色授权!",{icon:5});
            return;
        }

        layui.layer.open({
            title:"角色管理-角色授权",
            type:2,
            area:["500px","300px"],
            maxmin:true,
            content:ctx+"/module/toAddGrantPage?roleId="+datas[0].id
        })

    }


    //行监听  tool(saleChances) tool行  lay-filter="saleChances"容器id
    table.on('tool(roles)', function(obj){
        console.log(obj);
        var layEvent = obj.event;
        if(layEvent === "edit") {
            //跟新操作
            openAddOrUpdateRoleDialog(obj.data.id);

        }else if(layEvent === "del") {
            //行删除操作

            layer.confirm('确定删除该记录吗?',{icon:3},function (index){
                //确定删除,关闭
                layer.close(index);
                //请求后台
                $.ajax({
                    type: "post",
                    url: ctx+"/role/delete",
                    data:{
                        roleid: obj.data.id
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

    //打开弹出层窗口
    function openAddOrUpdateRoleDialog(id){
        var url  =  ctx+"/role/toSaleChanePage";
        var title="<h2>角色管理-角色添加</h2>";
        //saleChanceId==null添加,e修改
        if (id!=null && id!=""){
            title="<h2>角色管理-角色修改</h2>";
            url = url+"?id="+id;
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


});
