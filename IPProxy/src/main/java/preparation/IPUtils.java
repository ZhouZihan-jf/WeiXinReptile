package preparation;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pojo.IPMessage;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.System.out;

/**
 * @Author ���Ӻ�
 * @Date 2022/4/20 11:29
 * @Version 1.0
 * ���Դ�ip�Ƿ���Ч
 */
public class IPUtils {
    //����ip�Ƿ���Ч
    public static List<IPMessage> IPIsable(List<IPMessage> ipMessages) {
        String ip;
        String port;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        for(int i = 0; i < ipMessages.size(); i++) {
            ip = ipMessages.get(i).getIPAddress();
            port = ipMessages.get(i).getIPPort();

            HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
            RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(3000).
                    setSocketTimeout(3000).build();
            HttpGet httpGet = new HttpGet("https://www.baidu.com");
            httpGet.setConfig(config);

            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                    "q=0.9,image/webp,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                    "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

            try {
                response = httpClient.execute(httpGet);
            } catch (IOException e) {
                out.println("�����ô���" + ipMessages.get(i).getIPAddress() + ": " + ipMessages.get(i).getIPPort());
                ipMessages.remove(ipMessages.get(i));
                i--;
            }
        }

        try {
            httpClient.close();
            //response�п����ǿյ�
            if(response != null){
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ipMessages;
    }

    //��IP���й��ˣ�ѡȡ1000��IP���ٶ�����ǰ���ٵ�IP(����)�����������
    public static List<IPMessage> Filter(List<IPMessage> list) {
        List<IPMessage> IPlist = new ArrayList<>();

        Collections.sort(list, new Comparator<IPMessage>() {
            @Override
            public int compare(IPMessage o1, IPMessage o2) {
                return o1.getIPSpeed().compareTo(o2.getIPSpeed());
            }
        });

        //ֻ����������ǰ50�Ķ���
        for(int i = 0; i < list.size(); i++) {
            if(i < 50) {
                IPlist.add(list.get(i));
            }else {
                break;
            }
        }

        return IPlist;
    }

    /** �����κ�վ�� */
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
