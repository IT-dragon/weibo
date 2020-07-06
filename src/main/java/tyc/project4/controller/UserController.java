package tyc.project4.controller;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tyc.project4.pojo.Message;
import tyc.project4.pojo.MessageCode;
import tyc.project4.pojo.User;
import tyc.project4.service.UserService;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller类
 * @author tandashuai666
 */
@Controller
public class UserController {
    @Resource
    UserService userService;

    /**
     * 1.登录
     * @param name
     * @param pwd
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Message login(@RequestParam("name") String name, @RequestParam("pwd") String pwd){
        Message message=new Message();
        if (StringUtils.isBlank(name)|StringUtils.isBlank(pwd)){
            return  message.setLoginFailMessage("登录失败:信息输入并不完整!");
        }
        User userLogin=userService.login(new User(null,name,pwd));
        System.out.println(userLogin);
        if (userLogin!=null){
           message.setSuccessMessage("登录成功");
            Map<String,Object> map=new HashMap<>();
            map.put("info",userLogin);
            message.setData(map);
           return message;
        }else {
            message.setLoginFailMessage("登录失败:信息输入有误!");
            return message;
        }
    }

    /**
     * 2. read-1. 查 询 一 个 用 户 关 注 了 谁 ？
     * @param uid
     * @return
     */
    @PostMapping("/queryAttendUsers")
    @ResponseBody
    public Message queryAttendUsers(@RequestParam("id") String uid){
        Message message=new Message();
        if (StringUtils.isBlank(uid)){
            return  message.setLoginFailMessage("查询失败!");
        }
        List<User> userList=userService.queryAttendUsers(uid);
        if (userList.toArray().length>0){
            message.setSuccessMessage("查询成功");
            Map<String,Object> userHashMap=new HashMap<>();
            for (int i=0;i<userList.toArray().length;i++) {
                /**
                 * 以user-i:User(xx,xx,xx)的形式存储在map里
                 */
                userHashMap.put("user-"+i,userList.get(i));
            }
            message.setData(userHashMap);
            return message;
        }else {
            message.setErrorMessage("查询失败");
            return message;
        }
    }

    /**
     * 查询粉丝信息
     * @param uid
     * @return
     */
    @PostMapping("/queryConcernedUsers")
    @ResponseBody
    public Message queryConcernedUsers(@RequestParam("id") String uid){
        Message message=new Message();
        if (StringUtils.isBlank(uid)){
            return  message.setLoginFailMessage("查询失败!");
        }
        List<User> userList=userService.queryConcernedUsers(uid);
        if (userList.toArray().length>0){
            message.setSuccessMessage("成功");
            Map<String,Object> userHashMap=new HashMap<>();
            for (int i=0;i<userList.toArray().length;i++) {
                /**
                 * 以user-i:User(xx,xx,xx)的形式存储在map里
                 */
                userHashMap.put("user-"+i,userList.get(i));
            }
            message.setData(userHashMap);
            return message;
        }else {
            message.setErrorMessage("查询失败");
            return message;
        }
    }

    /**
     * 2.read-2: 用 户 A 关 注 了 用 户 B 吗 ？
     * @param uid1 当前用户id
     * @param uid2 查询用户id
     * @return
     */
    @PostMapping("/checkFollowRel")
    @ResponseBody
    public Message checkFollowRel(@RequestParam("uid1") String uid1,
                                    @RequestParam("uid2") String uid2) throws IOException {
        Message message=new Message();
        if (StringUtils.isBlank(uid1)|StringUtils.isBlank(uid2)){
            return  message.setLoginFailMessage("查询失败!");
        }
        Map map=new HashMap();
        map.put(uid1,uid2);
        User user=userService.queryUserInfo(uid2);
        System.out.println(user);
        if (userService.checkFollowRel(map)){
            System.out.println("您已关注");
            map.clear();
            map.put("id",user.getId());
            map.put("name",user.getName());
            message.setData(map);
            return message.setSuccessMessage("您已关注!");
        }else {
            message.setCode(MessageCode.NULL);
            message.setMessage("你还没有关注");
            map.clear();
            map.put("id",user.getId());
            map.put("name",user.getName());
            message.setData(map);
            return message;
        }
    }

    /**
     * 关注功能
     * @param uid1
     * @param uid2
     * @return
     */
    @PostMapping("/doFollow")
    @ResponseBody
    public Message doFollow(@RequestParam("uid1") String uid1,@RequestParam("uid2") String uid2) throws IOException {
        Message message=new Message();
        if (StringUtils.isBlank(uid1)|StringUtils.isBlank(uid2)){
            return  message.setLoginFailMessage("关注失败");
        }
        Map map=new HashMap();
        map.put(uid1,uid2);
        userService.doFollow(map);
        map.clear();
        User user=userService.queryUserInfo(uid2);
        map.put("id",user.getId());
        map.put("name",user.getName());
        message.setData(map);
        return message.setSuccessMessage("关注成功");
    }

    /**
     * 取关功能
     * @param uid1
     * @param uid2
     * @return
     */
    @PostMapping("/unFollow")
    @ResponseBody
    public Message unFollow(@RequestParam("uid1") String uid1,@RequestParam("uid2") String uid2) throws IOException {
        Message message=new Message();
        if (StringUtils.isBlank(uid1)|StringUtils.isBlank(uid2)){
            return  message.setLoginFailMessage("取关失败");
        }
        Map map=new HashMap();
        map.put(uid1,uid2);
        /**
         * 删除前保存一份信息用于删除浏览器缓存
         */
        User user=userService.queryUserInfo(uid2);
        userService.unFollow(map);
        map.clear();
        map.put("id",user.getId());
        map.put("name",user.getName());
        message.setData(map);
        return message.setSuccessMessage("取关成功");
    }
//
//    /**
//     * 2.注销
//     * @param session
//     */
//    @GetMapping("/logout")
//    public void logout(HttpSession session){
//        session.removeAttribute("user");
//    }

    /**
     * 跳转到first页面
     * @return
     */
    @GetMapping("/toFirst")
    public String toFirst(){
        return "first.html";
    }
    /**
     * 跳转到read1-2页面
     * @return
     */
    @GetMapping("/toPage1")
    public String toPage1(){
        return "read1-2.html";
    }
    /**
     * 跳转到read3页面
     * @return
     */
    @GetMapping("/toPage2")
    public String toPage2(){
        return "read3.html";
    }
    /**
     * 跳转到write1页面
     * @return
     */
    @GetMapping("/toPage3")
    public String toPage3(){
        return "write1.html";
    }
}
