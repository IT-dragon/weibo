// JavaScript Document by Richbox
function initTree(t) {
    var tree=document.getElementById(t);
    tree.style.display="none";
    var lis=tree.getElementsByTagName("li");
    for(var i=0;i<lis.length;i++) {
        lis[i].id="li"+i;
        var uls=lis[i].getElementsByTagName("ul");
        if(uls.length!=0) {
            uls[0].id="ul"+i;
            uls[0].style.display="none";
            var as=lis[i].getElementsByTagName("a");
            as[0].id="a"+i;
            as[0].className="folder";
            as[0].href="#"+i;
            as[0].tget=uls[0];
            as[0].onclick=function() {
                openTag(this,this.tget);
            }
        }
    }
    tree.style.display="block";
    var name=$.session.get("name");
    var id=$.session.get("id");
    var info=$('#info');
    info.html(id+"-"+name);
    info.show();
}
function openTag(a,t) {
    if(t.style.display=="block") {
        t.style.display="none";
        a.className="folder";
    } else {
        t.style.display="block";
        a.className="";
    }
}

/**
 * 所有刷新窗口的函数
 */
/**
 * 刷新窗口并初始化页面
 */
function flush1(){
    /**
     * 从localStorage获取用户session数据
     */
    var id=$.session.get('id');
    /**
     * 发起ajax异步请求
     */
    $.ajax({
            url: '/queryAttendUsers',
            type: 'post',
            data:{
                "id":id
            },
            success: function (data) {
                alert("查询成功！返回数组");
                if(data["code"]==2000){
                    var names=new Array();
                    var ids=new Array();
                    for (var key in data["data"]){
                        names.push(data['data'][key]["name"]);
                        ids.push(data["data"][key]['id']);
                    }
                    /**
                     * 关注的用户信息
                     */
                    $.session.set("a-names",names);
                    $.session.set("a-ids",ids);
                    $('#frameBord').attr('src','/toPage1');
                }else {
                    alert("查询失败!");
                }
            },
            error:function (XMLHttpRequest, textStatus) {

            }
        }
    );

}
function flush2(){
    $('#frameBord').attr('src','/toPage2');
}
function flush3(){
    $('#frameBord').attr('src','/toPage3');
}
function flush4(){
    $('#frameBord').attr('src','/toPage4');
}

/**
 * 初始化页面
 */
window.onload=function() {
    initTree("globalNav");
}