$(function () {
    loadModuleInfo(); 
});


var zTreeObj;
function loadModuleInfo() {
    $.ajax({
        type:"get",
        url:ctx+"/module/queryAllModules?roleId="+$("input[name='roleId']").val(),
        //url:ctx+"/module/queryAllModules",
        dataType:"json",
        success:function (data) {

            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },

                 callback: {
                     //用于捕获节点被点击的事件回调函数
                    onCheck: zTreeOnCheck
                    //onCheck: onclick
                }
            };
            //加载ztree树插件
            zTreeObj = $.fn.zTree.init($("#test1"),setting, data);
        }
    })
}

//当节点勾选或取消勾选时,调用的函数
function zTreeOnCheck(event, treeId, treeNode){
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    //zTreeObj.getCheckedNodes 获取所有的节点集合数组
    var nodes= zTreeObj.getCheckedNodes(true);
    //{id: 1, pId: null, name: "营销管理", children: Array(1), level: 0, …}
    //拼接选中的节点 当前的id
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }
    console.log(mids);
    //隐藏域id
    var roleId=$("input[name='roleId']").val();
    //ajax请求后台
    $.ajax({
        type: "post",
        url: ctx+"/role/addGrant",
        data: mids +"&roleId="+roleId,
        success: function (data){
            console.log(data)
        }
    })

}


