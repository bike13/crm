layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    //提交按钮监听
    form.on("submit(addOrUpdateSaleChance)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {
            icon: 16, //图标
            time: false,
            shade: 0.8
        });

        var url=ctx + "/sale_chance/add";

        var saleChanceId=$("[name='id']").val();
        if (saleChanceId!= null&&saleChanceId!=''){
            url=ctx + "/sale_chance/update";
        }


        //post提交方式
        $.post(url, data.field, function (res) {
            //登录成功
            if (res.code == 200) {
                top.layer.msg("操作成功！",{icon:6});
                top.layer.close(index);
                //关闭弹出层
                layer.closeAll("iframe");
                //刷新父页面
                parent.location.reload();
            } else {
                layer.msg(
                    res.msg, {
                        icon: 5
                    }
                );
            }
        });
        //阻止表单提交
        return false;
    });

    /**
     * 关闭弹出框
     */
    $("#closeBtn").click(function (){
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 加载下拉框
     */
    $.ajax({
        type:"post",
        url: ctx+"/user/queryAllSales",
        data:{},
        success:function (data){
            //console.log(data)
            if (data!=null){
                //当隐藏域id与循环id相同时设置选中
                var assignManId=$("#assignManId").val()
                var opt=null;
                for (var i=0;i<data.length;i++){
                    if (assignManId ==data[i].id){
                        opt="<option value='"+ data[i].id +"' selected>"+data[i].uname +"</option>";
                    }else {
                        opt="<option value='"+ data[i].id +"'>"+data[i].uname +"</option>";
                    }
                    $("#assignMan").append(opt);
                }
            }
            //重新渲染下拉框
            layui.form.render("select")
        }
    })


});