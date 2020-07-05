package tyc.project4.service;

import org.springframework.stereotype.Service;
import tyc.project4.db.HbaseUtil;
import tyc.project4.db.HbaseUtilImpl;
import tyc.project4.pojo.User;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * UserService的实现类，实现weibo业务逻辑
 * @author tandashuai666
 */
@Service
public class UserServiceImpl implements UserService {
    /**
     * 实例化Hbase工具类
     */
    @Resource
    private HbaseUtil hbaseUtilImpl;

    /**
     * read-1. 查 询 一 个 用 户 关 注 了 谁 ？
     *
     * @param uid
     * @return
     */
    @Override
    public List<User> queryAttendUsers(String uid){
        if ("".equals(uid)|uid==null){
            return null;
        }
        List<User> userList=null;
        try {
            List<String> list= hbaseUtilImpl.queryUidsByUid(uid,true);
            userList= hbaseUtilImpl.queryUserInfoByUids(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * read-2. 用 户 A 关 注 了 用 户 B 吗
     *遍历A的attends和B的concerneds,匹配两个值
     * @param map
     * @return
     */
    @Override
    public Boolean checkFollowRel(Map<String, String> map) {
        Boolean flag_1=false;
        Boolean flag_2=false;
        List<String> uids;
        if (map.isEmpty()){
            return false;
        }
        for (String s : map.keySet()) {
            try {
                uids= hbaseUtilImpl.queryUidsByUid(s,true);
                if (uids!=null){
                    flag_1=uids.stream().anyMatch(s1 -> s1.equals(map.get(s)));
                }else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (String s : map.values()) {
            System.out.println(s);
            try {
                uids= hbaseUtilImpl.queryUidsByUid(s,false);
                if (uids!=null){
                    flag_2=uids.stream().anyMatch(s1 -> map.containsKey(s1));
                }else {
                    return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag_1&&flag_2;
    }

    /**
     * read-3. 谁 关 注 了 用 户 A？
     *
     * @param uid
     * @return
     */
    @Override
    public List<User> queryConcernedUsers(String uid) {
        if ("".equals(uid)|uid==null){
            return null;
        }
        List<User> userList=null;
        try {
            List<String> list= hbaseUtilImpl.queryUidsByUid(uid,false);
            userList= hbaseUtilImpl.queryUserInfoByUids(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * write-1. 用 户 A 关 注 了 用 户 B
     *
     * @param map
     */
    @Override
    public void doFollow(Map<String, String> map) {
        try {
            hbaseUtilImpl.addInRelation(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write-2. 用 户 A 取 消 关 注 用 户 B
     *
     * @param map
     */
    @Override
    public void unFollow(Map<String, String> map) {
        try {
            hbaseUtilImpl.deleteInRelation(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        if (user==null){
            System.out.println("null");
            return null;
        }
        User user1 = null;
        try {
            user1= hbaseUtilImpl.login(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user1;
    }

    /**
     * 查询用户信息
     *
     * @param uid
     * @return
     */
    @Override
    public User queryUserInfo(String uid) throws IOException {
        return hbaseUtilImpl.queryUserInfo(uid);
    }
}
