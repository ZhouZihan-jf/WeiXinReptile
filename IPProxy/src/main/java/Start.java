import mongo.MongoOpreation;
import pojo.IPMessage;
import preparation.URLFecter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Start {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //�����б�洢
        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
        //��ȡ����ip
        ipMessages = URLFecter.urlParse(ipMessages);
        //�־û���mongodb����
        MongoOpreation.clean();
        MongoOpreation.save(ipMessages);
    }
}
