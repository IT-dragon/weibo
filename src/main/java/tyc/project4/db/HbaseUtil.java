package tyc.project4.db;

import org.apache.hadoop.hbase.client.Result;
import tyc.project4.pojo.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HbaseUtil {
     static void initHbase() throws IOException {};
     User transToUser(Result result);
     void insertData(String tableName, Object obj) throws IOException;
     List queryAllDataByTableName(String tableName) throws IOException;
     void deleteDataByRowKey(String tableName, String rowKey) throws IOException;
     List<String> queryUidsByUid(String uid,Boolean actFlag) throws IOException;
     List<User> queryUserInfoByUids(List<String> ids) throws IOException;
     void addInRelation(Map<String,String> map) throws IOException;
     void deleteInRelation(Map <String,String> map) throws IOException;
     User login(User user_WillJustify) throws IOException;

     /**
      * 根据用户id查询用户信息
      * @param uid
      * @return
      */
     User queryUserInfo(String uid) throws IOException;
     static void close() throws IOException{};
}
