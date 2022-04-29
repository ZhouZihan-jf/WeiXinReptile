package mongo;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.Configuration;
import org.bson.Document;
import pojo.IPMessage;

import java.util.List;

public class MongoOpreation {
    //创建连接
    private static MongoClient mongoClient = new MongoClient(Configuration.MONGO_HP);
    //打开数据库
    private static MongoDatabase weixin_test = mongoClient.getDatabase(Configuration.MONGO_DBNAME);
    //打开集合
    private static MongoCollection weixinCollection = weixin_test.getCollection(Configuration.MONGO_PROXYCOLLECTION);
    //保存
    public static void save(List<IPMessage> ipMessages){
        for(IPMessage ipMessage : ipMessages){
            //转成一条文档(先转成json，再转成document)
            Document document = Document.parse(JSONObject.toJSONString(ipMessage));
            //插入数据库
            weixinCollection.insertOne(document);
        }
    }
    //清空集合
    public static void clean(){
        weixinCollection.drop();
    }
}
