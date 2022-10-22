layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

//上述的submit(*)中的 * 号为事件过滤器的值，是在你绑定执行提交的元素时设定的
    //监听确认保存按钮
    form.on('submit(saveBtn)', function(data){

        //执行ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/updatepwd",
            data:{
                //data.field表示提交后端的信息
                oldpwd :data.field.old_password,
                newpwd :data.field.new_password,
                repeatpwd :data.field.again_password
            },
            success:function (result){

                if (result.code==200){
                    layer.msg("用户修改成功,系统在三秒后退出",function (){
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        window.parent.location.href=ctx+"/index"
                    })
                }else {
                    layer.msg(result.msg,{icon:5})
                }
            }
        })
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
        //return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

});