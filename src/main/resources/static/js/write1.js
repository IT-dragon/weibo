function init() {
    /**
     * chrome支持H5的localStorage技术
     * localStorage内只能以字符串形式存储
     * @type {string[]}
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
                    var dom=$('#nav');


                    /**
                     * 使用jq的each方法遍历数组
                     */
                    $.each(ids,function (index,value) {
                        $.each(names,
                            function (index2,value2) {
                                if (index==index2){
                                    /**
                                     * id-x作为标签的id值
                                     * name-x作为标签的id值
                                     */
                                    dom.after(
                                        dom.after("<tr><td>"+value+
                                            "</td>"+"<td>"+value2+"</td>"+"<td></td>"+
                                            "</tr>")
                                    )
                                }
                            }
                        );
                    });
                }else {
                    alert("查询失败!");
                }
            },
            error:function (XMLHttpRequest, textStatus) {

            }
        }
    );


}

/**
 * 为数组对象添加方法
 * @param val
 */
Array.prototype.removeByValue = function(val) {
    for(var i=0; i<this.length; i++) {
        if(this[i] == val) {
            this.splice(i, 1);
            break;
        }
    }
}
/**
 * 用ajax查询关注用户与否并返回信息
 */
function queryInfoById(){
    /**
     * 当前用户的id
     */
    var uid1=$.session.get("id");
    /**
     * 需要查询的id
     * @type {jQuery}
     */
    var uid2=$('#textfield').val();
    $.ajax(
        {
            url:'/checkFollowRel',
            type: 'post',
            data:{'uid1':uid1,'uid2':uid2},
            success:function(data){
                $.session.set('temp-id',data['data']['id']);
                $.session.set('temp-name',data['data']['name']);
                $('.result').html(data['message']+":"+data['data']['name']);
                $('.result').css({'color':'red'});
            },
            error:function (XMLHttpRequest, textStatus) {

            }
        }
    );
}

/**
 * 关注
 */
function follow(){
    var uid1=$.session.get("id");
    var uid2=$('#textfield').val();
    if (uid1==undefined||uid2!=$.session.get("temp-id")){
        alert("关注前请搜索ID");
    }else {
        $.ajax(
            {
                url:'/doFollow',
                type: 'post',
                data:{'uid1':uid1,'uid2':uid2},
                success:function(data){
                    if (data['code']==2000){
                        var ids=$.session.get('a-ids').split(',');
                        var names=$.session.get('a-names').split(',');
                        /**
                         * 保证缓存中不会写入重复值
                         */
                        if (ids.indexOf(data['data']['id'])==-1){
                            ids.push(data['data']['id']);
                            $.session.set("a-ids",ids);
                        }else if (names.indexOf(data['data']['name'])==-1){
                            names.push(data['data']['name']);
                            $.session.set("a-names",names);
                        }
                        /**
                         * 页面刷新
                         */
                        location.reload();
                        $('.result').html(data['message']);
                        $('.result').css({'color':'red'});
                    }else{
                        $('.result').html(data['message']);
                        $('.result').css({'color':'red'});
                    }
                },
                error:function (XMLHttpRequest, textStatus) {

                }
            }
        );
    }
}
/**
 * 取关
 */
function unFollow(){
    var uid1=$.session.get("id");
    var uid2=$.session.get("temp-id");
    if (uid1==undefined||uid2!=$('#textfield').val()){
        alert("取关前请搜索ID");
    }else {
        $.ajax(
            {
                url:'/unFollow',
                type: 'post',
                data:{'uid1':uid1,'uid2':uid2},
                success:function(data){
                    if (data['code']==2000){
                        var ids=$.session.get('a-ids').split(',');
                        var names=$.session.get('a-names').split(',');
                        /**
                         * 删除缓存
                         */
                        ids.removeByValue(data['data']['id']);
                        names.removeByValue(data['data']['name']);
                        $.session.set("a-ids",ids);
                        $.session.set("a-names",names);
                        /**
                         * 页面刷新
                         */
                        location.reload();
                        $('.result').html(data['message']);
                        $('.result').css({'color':'red'});
                    }else{
                        $('.result').html(data['message']);
                        $('.result').css({'color':'red'});
                    }
                },
                error:function (XMLHttpRequest, textStatus) {

                }
            }
        );
    }
}
/**
 * read1-2页面初始化
 */
$(function() {
        init();
    }
);
