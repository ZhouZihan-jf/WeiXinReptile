package preparation;

import config.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pojo.IPMessage;

import java.io.IOException;
import java.util.List;

/**
 * @Author ���Ӻ�
 * @Date 2022/4/20 11:40
 * @Version 1.0
 */

public class URLFecter {
    private static String  html = Configuration.PROXY_PLATFORM;

    //ʹ�ñ���IP��ȡ�������վ�ĵ�һҳ
    public static List<IPMessage> urlParse(List<IPMessage> list) throws IOException, ClassNotFoundException {
        //��html������DOM�ṹ
        Document document = Jsoup.connect(html).get();
        //��ȡ����Ҫ������
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

        //�鿴������ip�Ƿ����
        list = IPUtils.IPIsable(list);
        //��ip���ٶ�����
        IPUtils.Filter(list);

        if(list.isEmpty()){
            System.out.println("û���������ʵĴ���ip");
            return list;
        }else{
            System.out.println("�����˺��ʵĴ���ip");
            return list;
        }
    }
}

