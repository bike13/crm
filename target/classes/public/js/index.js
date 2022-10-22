layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on('submit(login)', function(data){
        //上述的submit(*)中的 * 号为事件过滤器的值，是在你绑定执行提交的元素时设定的

        //执行ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                userName :data.field.userName,
                userPwd :data.field.userPwd
            },
            success:function (result){
                console.log(result)
                //判断登录成功
                /*设置用户是登录状态
                    1.利用session会话
                        保存用户信息，如果会话存在，则用户是登录状态;否则是非登录状态
                        缺点:服务器关闭，会话则会失效
                    2.利用cookie
                        保存用户信息，cookie未失效。则用户是登录状态
                */
                if (result.code==200){

                    layer.msg("登录成功",function (){
                        if($("#rememberMe")){
                            $.cookie("userIdStr",result.result.userIdStr,{expires:7});
                            $.cookie("userName",result.result.userName,{expires:7});
                            $.cookie("trueName",result.result.trueName,{expires:7});
                        }else {
                            $.cookie("userIdStr",result.result.userIdStr);
                            $.cookie("userName",result.result.userName);
                            $.cookie("trueName",result.result.trueName);
                        }
                        window.location.href=ctx+"/main"
                    })
                }else {
                    layer.msg(result.msg,{icon:5})
                }
            }
        })
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });


});