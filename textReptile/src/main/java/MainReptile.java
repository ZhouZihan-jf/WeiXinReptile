import models.IpProxy;
import mongo.MongoOperation;
import reptile.ReptileText;
import tools.IOTools;
import tools.bloom.SimpleBloomFilter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainReptile {
    //创建布隆过滤器
    private static SimpleBloomFilter bf = new SimpleBloomFilter();

    //程序主入口
    public static void main(String[] args) throws IOException, InterruptedException {
        //先获取可用代理ip列表
        List<IpProxy> proxies = MongoOperation.getProxyByMongo();
        //设置代理ip
        //System.getProperties().setProperty("proxytSet","true");
        //System.getProperties().setProperty("http.proxyHost",proxies.get(0).getHost());
        //System.getProperties().setProperty("http.proxyPort",proxies.get(0).getPort());

        //获取要爬取的url列表
        List<String> urls = IOTools.importUrls();

        //同样要锻炼布隆过滤器，爬过的url就不重复爬取了
        for (String u : urls){
            bf.addValue(u);
        }

        String result = null;
        result = ReptileText.reptileText(urls,bf);
        System.out.println(result);
    }
}
