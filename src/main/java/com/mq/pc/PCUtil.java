package com.mq.pc;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author mq
 * @description: TODO
 * @title: PCUtil
 * @projectName pachong
 * @date 2020/12/3017:10
 */
public class PCUtil {

    static int count = 0;

    public static void main(String[] args) throws IOException {
        int page = 50;
        //http://tiebapic.baidu.com/forum/pic/item/61d98b18367adab4b95571059cd4b31c8601e434.jpg
        //https://tieba.baidu.com/f?kw=%E4%BC%AA%E5%A8%98&ie=utf-8&pn=0
        String tburl = "https://tieba.baidu.com/f?ie=utf-8&kw=%E4%BC%AA%E5%A8%98";
        String imgurl="https://www.uquq.cn/tools/img/api.php?fl=dongman&gs=images";
        HttpRequest httpRequest = HttpRequest.get(imgurl);
        HttpResponse execute = httpRequest.timeout(20000).execute();
        String location = execute.header("location");
        HttpRequest httpRequest1 = HttpRequest.get(imgurl);
        System.out.println(location);
        String body = httpRequest.timeout(20000).execute().body();
        System.out.println(body);
//        copy();
        while (count <= 10000) {
            sendGet(imgurl);
        }
    }

    public static void sendGet(String url) throws IOException {

        long l = System.currentTimeMillis();
        URL uri = new URL(url);

        StringBuilder sb = new StringBuilder();

        URLConnection connection = uri.openConnection();

        connection.connect();
        InputStream is = connection.getInputStream();
        File file = new File("imgs/" + ++count+"-"+System.currentTimeMillis() + ".jpg");
//        if(!file.exists()){
//            file.mkdirs();
//        }
        OutputStream os = new FileOutputStream(file);
        byte[] arr = new byte[1024];
        int read;
        while ((read = is.read(arr)) != -1) {
            os.write(arr,0,read);
        }
        long l1 = System.currentTimeMillis();
        System.out.print("复制图片用时（毫秒）：");
        System.out.println(l1 - l);
        os.flush();
        os.close();
        is.close();

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//        FileOutputStream fileOutputStream = new FileOutputStream(file);//true代表追加写入内容
//
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
//
//        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
////        fileOutputStream
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            sb.append(line);
//        }
//        bufferedWriter.write(sb.toString() + ",\r\n");
//        //记得flush不然文件不完整
//        bufferedWriter.flush();
    }


    public static void copy() {
        File file = new File("mq.txt");
        byte[] arr = new byte[1024];
        InputStream is;
        StringBuilder sb = new StringBuilder();
        OutputStream os = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            is = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(is));
            os = new FileOutputStream(new File("mq1.txt"));
            bw = new BufferedWriter(new OutputStreamWriter(os));

            while ((is.read(arr)) != -1) {
                os.write(arr);
            }
            os.flush();
//            String line;
//            while ((line = br.readLine()) != null){
//                sb.append(line);
//            }
//            System.out.println(sb.toString());
//            bw.write(sb.toString());
//            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyimg() throws IOException {
        StringBuilder sb = new StringBuilder();
        File olfFile = new File("974.jpg");
        File newFile = new File("111.jsp");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(olfFile)));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(newFile));
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedWriter.write(sb.toString());
        bufferedWriter.flush();
    }
}
