package preparation;

import config.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pojo.IPMessage;

import java.io.IOException;
import java.util.List;

/**
 * @Author 周子涵
 * @Date 2022/4/20 11:40
 * @Version 1.0
 */

public class URLFecter {
    private static String  html = Configuration.PROXY_PLATFORM;

    //使用本机IP爬取快代理网站的第一页
    public static List<IPMessage> urlParse(List<IPMessage> list) throws IOException, ClassNotFoundException {
        //将html解析成DOM结构
        Document document = Jsoup.connect(html).get();
        //提取所需要的数据
        Elements trs = document.select("table[class=table table-bordered table-striped]").select("tbody").select("tr");

        for (int i = 1; i < trs.size(); i++) {
            IPMessage ipMessage = new IPMessage();
            String ipAddress = trs.get(i).select("td").get(0).text();
            String ipPort = trs.get(i).select("td").get(1).text();
            String ipSpeed = trs.get(i).select("td").get(5).text();

            ipMessage.setIPAddress(ipAddress);
            ipMessage.setIPPort(ipPort);
            ipMessage.setIPSpeed(ipSpeed);

            list.add(ipMessage);
        }

        //查看爬到的ip是否可用
        list = IPUtils.IPIsable(list);
        //对ip按速度排序
        IPUtils.Filter(list);

        if(list.isEmpty()){
            System.out.println("没有整到合适的代理ip");
            return list;
        }else{
            System.out.println("整到了合适的代理ip");
            return list;
        }
    }
}

