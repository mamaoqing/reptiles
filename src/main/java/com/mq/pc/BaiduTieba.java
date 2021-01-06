package com.mq.pc;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author mq
 * @description: 爬取百度贴吧图片
 * @title: PCUtil
 * @projectName pachong
 * @date 2020/12/3017:55
 */
public class BaiduTieba {

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    /**
     * 贴吧主路径
     */
    private static final String TB_BASE_URL = "https://tieba.baidu.com";
    /**
     * 用于存放url
     */
    private static Map<String, String> URL_MAP;
    /**
     * 图片保存的路径
     */
    private static final String IMAGE_SAVE_DIRECT = "tieba";
    /**
     * HttpClient对象
     */
    private static CloseableHttpClient httpClient = null;
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

    /**
     * 获取指定贴吧的全部内容
     *
     * @param key           贴吧关键字
     * @param maxPage       最大页码
     * @param onlySeeLz     是否只看楼主
     * @param addWaterImage 是否添加水印
     * @throws Exception e
     */
    public static void getHttpUrl(String key, int maxPage, boolean onlySeeLz, boolean addWaterImage) throws Exception {
        //初始化httpclient对象
        httpClient = HttpClients.createDefault();

        //开始按照页面爬取内容
        while (maxPage > 0) {
            System.out.println("=============正在处理第" + maxPage + "页===================");
            //每页有50条数据
            int pageIndex = (maxPage - 1) * EVERY_PAGE_COUNT_SIZE;
            //路径
            String spaderUrl = TB_BASE_URL + "/f?kw=" + key + "&ie=utf-8&pn=" + pageIndex;
            System.out.println("spaderUrl====    " + spaderUrl);
            String responseContent = getHtmlContent(spaderUrl);
            processHtml(responseContent, onlySeeLz);
            maxPage--;
        }
        httpClient.close();
        downLoadImage(addWaterImage);
    }

    /***
     * 根据url获取页面源码内容
     * @param url url
     * @return 页面源码内容
     * @throws Exception e
     */
    public static String getHtmlContent(String url) throws Exception {
        //get方式获取页面内容
        HttpGet get = new HttpGet(url);
        get.setConfig(REQUEST_CONFIG);
        HttpEntity entity = httpClient.execute(get).getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    /***
     * 处理html内容
     * @param responseContent html内容
     * @param onlySeeLz 是否只看楼主
     */
    private static void processHtml(String responseContent, boolean onlySeeLz){
        Document doc = Jsoup.parse(responseContent);
        //获取所有 class=j_th_tit 的 a标签;帖子的具体连接
        Elements urls = doc.select("a.j_th_tit");
        for (Element e : urls) {
            //帖子标题
            String tText = e.text();
            System.out.println("帖子标题：" + tText);
            //帖子连接
            String tUrl = e.attr("href");
            tUrl = TB_BASE_URL + "" + tUrl;
            //只看楼主
            if (onlySeeLz) {
                tUrl = tUrl + "?see_lz=1";
            }
            //将获取到的帖子url放入Map
            URL_MAP.put(tText, tUrl);
        }
    }

    /***
     * 获取每个帖子内容中的图片信息
     * @param addWaterImage 是否加水印
     * @throws Exception e
     */
    private static void downLoadImage(boolean addWaterImage) throws Exception {
        for (String str : URL_MAP.values()) {
            //帖子的url
            System.out.println("帖子的url===   " + str);
            Document doc = Jsoup.connect(str).get();
            //帖子中 class=img.BDE_Image的元素
            Elements images = doc.select("img.BDE_Image");
            for (Element e : images) {
                //获取图片url
                String imageUrl = e.attr("src");
                System.out.println("imageUrl============ " + imageUrl);
                saveImage(imageUrl, addWaterImage);
            }
        }
    }

    /**
     * 将图片保存到本地
     *
     * @param imageUrl      imageUrl 图片url
     * @param addWaterImage addWaterImage 是否打印水印
     * @throws Exception e
     */
    private static void saveImage(String imageUrl, boolean addWaterImage) throws Exception {
        //每天创建一个目录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String filePath = sdf.format(new Date());
        File imageFile = new File(IMAGE_SAVE_DIRECT + "//" + filePath);
        if (!imageFile.exists()) {
            if (imageFile.mkdirs()) {
                System.out.println("---------创建目录成功-------------");
            }
        }
        //随机生成图片名称
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(IMAGE_SAVE_DIRECT + "//" + filePath + "//" + fileName + ".jpg");
        //            addWaterImage(url, os);
        if (!addWaterImage) {
            saveImageWithoutWaterImage(is, os);
        }
    }

    private static void saveImageWithoutWaterImage(InputStream is, OutputStream os) throws Exception {
        byte[] buff = new byte[1024];
        int readed;
        while ((readed = is.read(buff)) != -1) {
            os.write(buff, 0, readed);
        }
        is.close();
        os.close();
    }

    /***
     * 打印水印
     * @throws Exception e
     */
//    private static void addWaterImage(URL sourceImagePath, OutputStream os) throws Exception {
//        //根据图片路径生成图片对象。获取图片的宽度高度
//        Image image = ImageIO.read(sourceImagePath);
//        int width = image.getWidth(null);
//        int height = image.getHeight(null);
//
//        //根据图片的宽高，生成画布，将原图画到画布
//        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics2d = bufferedImage.createGraphics();
//        graphics2d.drawImage(image, 0, 0, width, height, null);
//
//        //水印图片
//        Image waterImage = ImageIO.read(new File(WATER_IMAGE_PATH));
//        int waterImageWidth = waterImage.getWidth(null);
//        int waterImageHeight = waterImage.getHeight(null);
//
//        //水印透明设置
//        graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, WATER_IMAGE_ALPHA));
//        //旋转 rotate(选中度数，圆心x坐标，圆心y坐标)
//        graphics2d.rotate(Math.toRadians(WATER_IMAGE_RADIANS), bufferedImage.getWidth() >> 1, bufferedImage.getHeight() >> 1);
//
//        // 循环打印水印图片
//        int waterImageX = -width / 2;
//        while (waterImageX < width * 1.5) {
//            int waterImageY = -height / 2;
//            while (waterImageY < height * 1.5) {
//                graphics2d.drawImage(waterImage, waterImageX, waterImageY, null);
//                waterImageY += waterImageHeight + WATER_IMAGE_MARGIN_Y;
//            }
//            waterImageX += waterImageWidth + WATER_IMAGE_MARGIN_X;
//        }
//        graphics2d.dispose();
//
//        //创建图像编码工具类
//        JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
//        //使用图像编码工具类，输出缓存图像到目标文件
//        en.encode(bufferedImage);
//        os.close();
//    }

    public static void main(String[] args) {
        try {
            URL_MAP = new HashMap<String, String>();
            String key = "李沁";
            int maxPage = 5;
            getHttpUrl(key, maxPage, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}