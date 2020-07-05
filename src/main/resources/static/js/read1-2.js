function init() {
    /**
     * chrome支持H5的localStorage技术
     * localStorage内只能以字符串形式存储
     * @type {string[]}
     */
    var names=$.session.get("a-names").split(',');
    var ids=$.session.get("a-ids").split(',');
    var dom=$('#nav');
    /**
     * 使用jq的each方法遍历数组
     */
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
 * 用ajax查询关注用户与否
 */
function checkFollowRel(){
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
                if (data['code']==2000){
                    $('.result').html(data['message']);
                    $('.result').css({'color':'red'});

                }else if (data['code']==2001){
                    $('.result').html(data['message']);
                    $('.result').css({'color':'red'});
                }
            },
            error:function (XMLHttpRequest, textStatus) {

            }
        }
    );
}
/**
 * read1-2页面初始化
 */
$(function() {
      init();
    }
);
