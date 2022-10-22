layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 关闭弹出框
     */
    $("#closeBtn").click(function (){
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    //表单提交监听(绑定按钮lay-filter="addOrUpdateCusDevPlan">确认)
    form.on("submit(addOrUpdateCusDevPlan)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //弹出loading
        var url=ctx + "/cus_dev_plan/add";
        if( $("input[name='id']").val() ){
            url=ctx + "/cus_dev_plan/update";
        }

        $.post(url, data.field, function (res) {
            if (res.code == 200) {

                //关闭加载层
                top.layer.close(index);
                top.layer.msg("操作成功！",{icon:6});
                //关闭弹出层
                layer.closeAll("iframe");
                //刷新父页面
                parent.location.reload();

            } else {
                top.layer.msg(res.msg,{icon:5});
            }
        });
        return false;
    });


});