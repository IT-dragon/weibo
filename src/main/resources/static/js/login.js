function isEmpty(strIn) {
    if (strIn === undefined) {
        return true;
    } else if (strIn == null) {
        return true;
    } else if (strIn == "") {
        return true;
    } else {
        return false;
    }
}

function submit(){
    alert('submit');
    var name=$("#username").val();
    var pwd=$("#pwd").val();
    var text=$("#text");
    if(isEmpty(name)|isEmpty(pwd)){
        text.html("信息不完整，请重新输入！");
        text.show();
        text.css({"color":"red"});
    }else{
        $.ajax({
                url: '/login',
                type: 'post',
                data:{
                    "name":name,
                    "pwd":pwd
                },
                success: function (data) {
                    //登录成功跳转到展示页面
                    if(data["code"]==2000){
                        var col=data["data"];
                        $.session.set("id",col["info"]["id"]);
                        $.session.set("name",col["info"]["name"]);
                        window.location.href='toFirst';
                    }else {
                        text.show();
                        text.html(data["message"]);
                    }
                },
                error:function (XMLHttpRequest, textStatus) {
                    text.show();
                    text.html(textStatus+"-登录失败！");
                }
            }
        )
    }
}
