package tyc.project4.service;

import tyc.project4.pojo.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 业务处理接口
 * @author tandashuai666
 */
public interface UserService {
    /**
     * read-1. 查 询 一 个 用 户 关 注 了 谁 ？
     * @param uid
     * @return
     */
    List<User> queryAttendUsers(String uid);

    /**
     * read-2. 用 户 A 关 注 了 用 户 B 吗
     * @param map
     * @return
     */
    Boolean checkFollowRel(Map<String,String> map);

    /**
     * read-3. 谁 关 注 了 用 户 A？
     * @param uid
     * @return
     */
    List<User> queryConcernedUsers(String uid);

    /**
     * write-1. 用 户 A 关 注 了 用 户 B
     * @param map
     */
    void doFollow(Map<String,String> map);

    /**
     * write-2. 用 户 A 取 消 关 注 用 户 B
     * @param map
     */
    void unFollow(Map<String,String> map);

    /**
     * 用户登录
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 查询用户信息
     * @param uid
     * @return
     */
    User queryUserInfo(String uid) throws IOException;
}
