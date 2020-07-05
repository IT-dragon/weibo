function init() {
    /**
     * chrome支持H5的localStorage技术
     * localStorage内只能以字符串形式存储
     * @type {string[]}
     */

    var dom=$('#nav');
    /**
     * 使用jq的each方法遍历数组
     */
    var id=$.session.get("id");
    $.ajax({
            url: '/queryConcernedUsers',
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
                     * 粉丝用户信息
                     */
                    $.session.set("b-names",names);
                    $.session.set("b-ids",ids);
                }else {
                    alert("查询失败!");
                }
            },
            error:function (XMLHttpRequest, textStatus) {

            }
        }
    );
    var names=$.session.get("b-names").split(',');
    var ids=$.session.get("b-ids").split(',');
    $.each(ids,function (index,value) {
        $.each(names,
            function (index2,value2) {
                if (index==index2){
                    dom.after("<tr><td>"+value+
                        "</td>"+"<td>"+value2+"</td>"+"</tr>");
                }
            }
        );
    });
}

/**
 * read3页面初始化
 */
$(function() {
        init();
    }
);
