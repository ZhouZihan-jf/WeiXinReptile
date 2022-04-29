package mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import config.Configuration;
import models.IpProxy;
import models.TextModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MongoOperation {
    //创建连接
    private static MongoClient mongoClient = new MongoClient(Configuration.MONGO_HP);
    //打开数据库
    private static MongoDatabase weixin_test = mongoClient.getDatabase(Configuration.MONGO_DBNAME);
    public static void storageByMongo(List<TextModel> list) {
        //打开集合
        MongoCollection textCollection = weixin_test.getCollection(Configuration.MONGO_TEXTCOLLECTION);

        for (TextModel text : list) {
            //转成一条文档(先转成json，再转成document)
            Document document = Document.parse(JSONObject.toJSONString(text));
            //插入数据库
            textCollection.insertOne(document);
        }
    }

    public static List<IpProxy> getProxyByMongo(){
        List<IpProxy> proxyIPs = new ArrayList<IpProxy>();

        //打开集合
        MongoCollection proxyCollection = weixin_test.getCollection(Configuration.MONGO_PROXYCOLLECTION);
        //创建迭代器
        MongoCursor<Document> doc = proxyCollection.find().iterator();
        //开始迭代
        while (doc.hasNext()){
            //获取一条记录，转成json对象
            JSONObject jsonObject = JSON.parseObject(doc.next().toJson());
            //创建对象接收
            IpProxy proxy = new IpProxy();
            proxy.setHost(jsonObject.getString("iPAddress"));
            proxy.setPort(jsonObject.getString("iPPort"));
            //加入列表
            proxyIPs.add(proxy);
        }
        return proxyIPs;
    }

    public static String queryTimeByTittle(String tittle){
        //打开集合
        MongoCollection urlsCollection = weixin_test.getCollection("urls");
        //设置条件
        Bson filter = Filters.eq("tittle",tittle);
        //查询
        MongoCursor<Document> cursor = urlsCollection.find(filter).iterator();
        while (cursor.hasNext()){
            //获取一条记录
            JSONObject jsonObject = JSON.parseObject(cursor.next().toJson());
            return jsonObject.getString("create_time");
        }
        return "";
    }
}
