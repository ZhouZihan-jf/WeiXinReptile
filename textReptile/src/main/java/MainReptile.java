import models.IpProxy;
import mongo.MongoOperation;
import reptile.ReptileText;
import tools.IOTools;
import tools.bloom.SimpleBloomFilter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainReptile {
    //������¡������
    private static SimpleBloomFilter bf = new SimpleBloomFilter();

    //���������
    public static void main(String[] args) throws IOException, InterruptedException {
        //�Ȼ�ȡ���ô���ip�б�
        List<IpProxy> proxies = MongoOperation.getProxyByMongo();
        //���ô���ip
        //System.getProperties().setProperty("proxytSet","true");
        //System.getProperties().setProperty("http.proxyHost",proxies.get(0).getHost());
        //System.getProperties().setProperty("http.proxyPort",proxies.get(0).getPort());

        //��ȡҪ��ȡ��url�б�
        List<String> urls = IOTools.importUrls();

        //ͬ��Ҫ������¡��������������url�Ͳ��ظ���ȡ��
        for (String u : urls){
            bf.addValue(u);
        }

        String result = null;
        result = ReptileText.reptileText(urls,bf);
        System.out.println(result);
    }
}
