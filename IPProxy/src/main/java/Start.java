import mongo.MongoOpreation;
import pojo.IPMessage;
import preparation.URLFecter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Start {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //创建列表存储
        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
        //爬取代理ip
        ipMessages = URLFecter.urlParse(ipMessages);
        //持久化到mongodb里面
        MongoOpreation.clean();
        MongoOpreation.save(ipMessages);
    }
}
