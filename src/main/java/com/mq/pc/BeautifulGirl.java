package com.mq.pc;

import cn.hutool.http.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author mq
 * @description: TODO
 * @title: BeautifulGirl
 * @projectName pachong
 * @date 2021/1/514:43
 */
public class BeautifulGirl {
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    /**
     * 贴吧主路径
     */
    private static final String BASE_URL = "http://www.meinvla.net";
    /**
     * 用于存放url
     */
    private static Map<String, String> URL_MAP;
    /**
     * 图片保存的路径
     */
    private static final String IMAGE_SAVE_DIRECT = "bg";
    /**
     * HttpClient对象
     */
    private static CloseableHttpClient httpClient = null;
    private static List<String> urlList = new ArrayList<String>();
    private static List<String> imagesUrl = new ArrayList<String>();
    /**
     * 每页有多少条帖子
     */
    private static final int EVERY_PAGE_COUNT_SIZE = 50;

    /**
     * 水印图片路径
     */
    private static final String WATER_IMAGE_PATH = "1.jpg";
    /**
     * 透明度
     */
    private static final float WATER_IMAGE_ALPHA = 0.5F;
    /**
     * X间距
     */
    private static final int WATER_IMAGE_MARGIN_Y = 100;
    /**
     * Y间距
     */
    private static final int WATER_IMAGE_MARGIN_X = 100;
    /**
     * 水印图片选中角度
     */
    private static final int WATER_IMAGE_RADIANS = 30;

    public static void main(String[] args) {
        try {
            down();
            for (String url : imagesUrl) {
                save("http://"+url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void down() throws Exception {
        //初始化httpclient对象
        httpClient = HttpClients.createDefault();
//        loadpage(getHtmlContent(BASE_URL));
        load(getHtmlContent(BASE_URL));
        for (String url : urlList) {
            Document doc = Jsoup.parse(getHtmlContent(BASE_URL + "" + url));
            Elements aurls = doc.select("img.img-responsive");
            for (Element e : aurls) {
//                System.out.println(e);
                String img_url = e.attr("src").replace("//", "");
                imagesUrl.add(img_url);
//                String text = e.attr("alt");
//                System.out.println("帖子标题====="+text);
//                System.out.println("图片地址=====："+img_url);
//                urlList.add(img_url);
            }
        }
    }

    public static void load(String responseContent) {
        Document doc = Jsoup.parse(responseContent);
        Elements parents = doc.select("img.img_lazy").parents();
        for (Element e : parents) {
            Elements select = e.select("a.effect5");
            for (Element ele : select) {
//                String text = ele.attr("title");
                String img_url = ele.attr("href");
                urlList.add(img_url);
            }
        }
        qcf();
    }

    public static void loadpage(String responseContent) {
        //effect5
        Document doc = Jsoup.parse(responseContent);
//        Elements urls = doc.select("img.img_lazy");
        Elements aurls = doc.select("img.img_lazy");
//        for (Element e : urls) {
//            String text = e.attr("alt");
//            String img_url = e.attr("data-original");
//            System.out.println("帖子标题"+text);
//            System.out.println("图片地址："+img_url);
//            urlList.add(img_url);
//        }
        for (Element e : aurls) {
            String text = e.attr("title");
            String img_url = e.attr("href");
            urlList.add(img_url);
        }
    }

    public static void qcf() {
        List<String> list = new ArrayList<String>();
        for (String s : urlList) {
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        urlList = list;
    }

    public static String getHtmlContent(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setConfig(REQUEST_CONFIG);
        HttpEntity entity = httpClient.execute(get).getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    public static void save(String imageUrl) throws IOException {
        //每天创建一个目录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String filePath = sdf.format(new Date());
        File imageFile = new File(IMAGE_SAVE_DIRECT + "//" + filePath);
        if (!imageFile.exists()) {
            if (imageFile.mkdirs()) {
                System.out.println("---------创建目录成功-------------");
            }
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        connection.setRequestProperty("Host","www.meinvla.net");
        connection.setRequestProperty("Referer","http://www.meinvla.net/");
        InputStream is = connection.getInputStream();
//        InputStream inputStream = url.openStream();
        OutputStream os = new FileOutputStream(IMAGE_SAVE_DIRECT + "//" + filePath + "//" + fileName + ".jpg");

        byte[] buff = new byte[1024];
        int readed;
        while ((readed = is.read(buff)) != -1) {
            os.write(buff, 0, readed);
        }
        is.close();
        os.close();
    }
}
