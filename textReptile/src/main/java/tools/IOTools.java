package tools;

import config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IOTools {
    private static File urlsFile = new File(Configuration.URLS_FILE);
    public static List<String> importUrls() throws IOException {
        List<String> urlList = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(urlsFile));

        String line = null;
        while ((line = br.readLine()) != null) {//使用readLine方法，一次读一行
            urlList.add(line);
        }

        br.close();

        return urlList;
    }
}
