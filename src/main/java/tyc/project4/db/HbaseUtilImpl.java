package tyc.project4.db;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.springframework.stereotype.Repository;
import tyc.project4.pojo.Relation;
import tyc.project4.pojo.User;

import java.io.IOException;
import java.util.*;

/**
 * 连接hbase数据库,用java-Api对hbase数据库进行操作
 * @author tandashuai666
 */
@Repository
public class HbaseUtilImpl implements HbaseUtil{
    /**
     * 默认数据库表名参数
     */
    private static final String TABLE_USER="project:user";
    private static final String TABLE_REL="project:relation";
    /**
     * 定义查询关注或是被关注用户的控制参数：true-关注的用户；false-被关注的用户
     */
    private static final Boolean ATTENDING=true;
    /**
     * hbase数据库连接
     */
    private static Connection conn;

    /**
     * 1.连接hbase数据库集群
     * @return
     * @throws IOException
     */
    public HbaseUtilImpl() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.19.129");
        configuration.set("hbase.master", "127.0.0.1:60000");
        conn=ConnectionFactory.createConnection(configuration);
    }

    /**
     * Result对象转换为User对象
     * @param result
     * @return
     */
    @Override
    public User transToUser(Result result) {
        User user = new User();
        for (Cell cell : result.rawCells()) {
            String rowKey = new String(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            String colName = new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String value = new String(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            user.setId(rowKey);
            if (colName.equals("name")) {
                user.setName(new String(value));
            } else if (colName.equals("pwd")) {
                user.setPwd(new String(value));
            }
        }
        return user;
    }
    /**
     * 2.1 添加一条记录
     * @param tableName
     * @param user
     * @throws IOException
     */
    @Override
    public void insertData(String tableName, Object obj) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        switch (tableName){
            case TABLE_USER:
                if (obj instanceof User){
                    User user=(User)obj;
                    Put put = new Put((user.getId()).getBytes());
                    //参数：1.列族名  2.列名  3.值
                    put.addColumn("info".getBytes(), "name".getBytes(), user.getName().getBytes()) ;
                    table.put(put);
                    put.addColumn("info".getBytes(), "pwd".getBytes(), user.getPwd().getBytes()) ;
                    table.put(put);
                }else {
                    System.out.println("插入数据类型与表不匹配");
                }
                break;
            case TABLE_REL:
                if (obj instanceof Relation){
                    Relation rel=(Relation)obj;
                    Put put = new Put((rel.getRowkey()).getBytes());
                    //参数：1.列族名  2.列名  3.值
                   for (String uid:(Collection<String>)rel.getAttends().values()){
                       put.addColumn("attends".getBytes(), uid.getBytes(), uid.getBytes()) ;
                       table.put(put);
                   }
                   for (String uid:(Collection<String>)rel.getConcerneds().values()){
                       put.addColumn("concerneds".getBytes(), uid.getBytes(), uid.getBytes()) ;
                       table.put(put);
                    }
                }else {
                    System.out.println("插入数据类型与表不匹配");
                }
                break;
                default:
                    System.out.println("输入的表名有误或者数据库中没有此表");
        }
    }

    /**
     * 2.2 根据表名查询所有的表数据，以链表形式返回结果
     * @param tableName
     * @throws IOException
     */
    @Override
    public List queryAllDataByTableName(String tableName) throws IOException {
        List list = new LinkedList<>();
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner results = table.getScanner(scan);
        switch (tableName) {
            case TABLE_USER:
                User user = null;
                for (Result result : results) {
                    user=transToUser(result);
                    list.add(user);
                }
            break;
        case TABLE_REL:
        for (Result result : results) {
            Relation rel = new Relation();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();
            for (Cell cell : result.rawCells()) {
                String rowKey = new String(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                String colFName = new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                String colName = new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                rel.setRowkey(rowKey);
                if (colFName.equals("attends")) {
                    map1.put(colName, colName);
                    rel.setAttends(map1);
                } else if (colFName.equals("concerneds")) {
                    map2.put(colName, colName);
                    rel.setConcerneds(map2);
                }
            }
            list.add(rel);
        }
        break;
        default:
        }
        return list;
    }

    /**
     * 2.3 根据rowKey删除一条记录
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    @Override
    public void deleteDataByRowKey(String tableName, String rowKey) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Delete delete=new Delete(rowKey.getBytes());
        table.delete(delete);
    }

    /**
     * 微博关注功能A-1，B,C：根据用户id查询返回所有用户关注的用户的id
     * @param uid
     * @return
     * @throws IOException
     */
    @Override
    public List<String> queryUidsByUid(String uid, Boolean actFlag) throws IOException {
        List<String> uids=new LinkedList<>();
        Table table=conn.getTable(TableName.valueOf(TABLE_REL));
        Get get=new Get(uid.getBytes());
        Result result=table.get(get);
        for (Cell cell : result.rawCells()) {
            String colFName = new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String value = new String(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            if (actFlag){
                if (colFName.equals("attends")) {
                    uids.add(value);
                }
            }else {
                if (colFName.equals("concerneds")) {
                    uids.add(value);
                }
            }
        }
        return uids;
    }

    /**
     * 微博关注功能A-2：根据id链表参数，以链表形式返回查询到的所有用户信息
     * @param ids
     * @return
     */
    @Override
    public List<User> queryUserInfoByUids(List<String> ids) throws IOException {
        List<User> users=new LinkedList<>();
        Table table=conn.getTable(TableName.valueOf(TABLE_USER));
        for (String id : ids) {
            Get get=new Get(id.getBytes());
            Result result=table.get(get);
            users.add(transToUser(result));
        }
        return users;
    }

    /**
     * 微博关注功能D: 用 户 A 关 注 了 用 户 B
     * @param map
     * @throws IOException
     */
    @Override
    public void addInRelation(Map <String,String> map) throws IOException {
        if(!map.isEmpty()){
            Table table=conn.getTable(TableName.valueOf(TABLE_REL));
            for (String s: map.keySet()) {
                Put put=new Put(s.getBytes());
                put.addColumn("attends".getBytes(),map.get(s).getBytes(),map.get(s).getBytes());
                table.put(put);
                put=new Put(map.get(s).getBytes());
                put.addColumn("concerneds".getBytes(),s.getBytes(),s.getBytes());
                table.put(put);
            }
        }
    }

    /**
     * 微博关注功能D: 用 户 A 取 消 关 注 了 用 户 B
     * @param map
     * @throws IOException
     */
    @Override
    public void deleteInRelation(Map <String,String> map) throws IOException {
        if(!map.isEmpty()){
            Table table=conn.getTable(TableName.valueOf(TABLE_REL));
            for (String s: map.keySet()) {
                Delete delete=new Delete(s.getBytes());
                delete.addColumn("attends".getBytes(),map.get(s).getBytes());
                table.delete(delete);
                delete=new Delete(map.get(s).getBytes());
                delete.addColumn("concerneds".getBytes(),s.getBytes());
                table.delete(delete);
            }
        }
    }

    /**
     * 用户登录
     * @param user_WillJustify
     * @return
     * @throws IOException
     */
    @Override
    public User login(User user_WillJustify) throws IOException {
        Table table=conn.getTable(TableName.valueOf(TABLE_USER));
        Scan scan=new Scan();
        ResultScanner results = table.getScanner(scan);
        for (Result result : results) {
            User  user=transToUser(result);
            if (user_WillJustify.getName().equals(user.getName())&&user_WillJustify.getPwd().equals(user.getPwd())){
                return user;
            }
        }
        return null;
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param uid
     * @return
     */
    @Override
    public User queryUserInfo(String uid) throws IOException {
        Table table=conn.getTable(TableName.valueOf(TABLE_USER));
        Get get=new Get(uid.getBytes());
        Result result=table.get(get);
        return transToUser(result);
    }

    /**
     * 关闭数据库连接
     * @throws IOException
     */
    public static void close() throws IOException {
        conn.close();
    }
}
