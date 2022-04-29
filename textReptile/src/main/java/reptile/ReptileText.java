package reptile;

import models.TextModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import mongo.MongoOperation;
import tools.bloom.SimpleBloomFilter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

//暂时用不到resp
public class ReptileText {
    //微信公众号文章域名
    public static final String WX_DOMAIN = "http://mp.weixin.qq.com";

    //爬取的主要方法
    public static String reptileText(List<String> urls, SimpleBloomFilter bf) throws IOException, InterruptedException {
        //检查链接是否合法
        int flag = 0;
        for (String url : urls) {
            String msg = checkUrl(url);
            if (msg != null) {
                flag++;
            }
        }
        if (flag != 0) {
            return "第" + flag + "条传入的url有问题";
        }
        //开始爬取
        List<TextModel> textList = new ArrayList<TextModel>();
        textList = getTextList(urls,bf);

        if (!textList.isEmpty()) {
            //把爬取的信息存入mongodb
            MongoOperation.storageByMongo(textList);
            return "爬取成功";
        } else {
            return "爬取结果为空";
        }
    }

    //检查链接
    public static String checkUrl(String url) {
        if (url == null) {
            return "请输入一个文章的链接";
        }
        if (!url.startsWith(WX_DOMAIN)) {
            return "这不是一个微信公众号链接";
        }
        return null;
    }

    /**
     * 微信公众号请求头设置
     */
    public static Map<String, String> getWxHeaderMap() {
        Map<String, String> map = new HashMap<>(new LinkedHashMap<>());
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        //map.put("Host", "mp.weixin.qq.com");
        //map.put("If-Modified-Since", "Sat, 04 Jan 2020 12:23:43 GMT");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36 Edg/100.0.1185.50");
        return map;
    }

    //解析公众号文章
    public static List<TextModel> getTextList(List<String> urls,SimpleBloomFilter bf) throws IOException, InterruptedException {
        List<TextModel> textList = new ArrayList<TextModel>();

        for (String url : urls) {
            //使用布隆过滤器过滤
            if(bf.contains(url)){
                System.out.println("该链接已爬过");
                continue;
            }
            //暂停几秒
            TimeUnit.SECONDS.sleep(3);
            //获得html
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .headers(getWxHeaderMap())
                    .get();

            String tittle = "";
            if(doc.getElementById("activity-name") != null){
                tittle = doc.getElementById("activity-name").text();
            }

            String time = MongoOperation.queryTimeByTittle(tittle);

            Elements contents = doc.select("span");
            String content = "";
            for (int i = 2; i < contents.size(); i++) {
                content += contents.get(i).text();
            }
            //System.out.println(content);

            TextModel text = new TextModel();
            text.setTittle(tittle);
            text.setTime(time);
            text.setContent(content);

            textList.add(text);
        }

        return textList;
    }

}
