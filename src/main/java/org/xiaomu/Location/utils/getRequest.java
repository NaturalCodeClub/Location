package org.xiaomu.Location.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class getRequest {
    public static String sendGet(String getUrl){
        try {
            String result = "";
            //1.通过在 URL 上调用 openConnection 方法创建连接对象
            URL url = new URL(getUrl);
            //此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection,
            //故此处最好将其转化为HttpURLConnection类型的对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //2.处理设置参数和一般请求属性
            //2.1设置参数
            //可以根据请求的需要设置参数
            conn.setRequestMethod("GET"); //默认为GET 所以GET不设置也行
            conn.setUseCaches(false);
            conn.setConnectTimeout(5000); //请求超时时间

            //2.2请求属性
            //设置通用的请求属性 更多的头字段信息可以查阅HTTP协议
            conn.setRequestProperty("content-type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("charset", "utf-8");

            //3.使用 connect 方法建立到远程对象的实际连接。
            conn.connect();

            //4.远程对象变为可用。远程对象的头字段和内容变为可访问。
            //4.1获取HTTP 响应消息获取状态码
            if (conn.getResponseCode() == 200) {
                //4.2获取响应的头字段
                Map<String, List<String>> headers = conn.getHeaderFields();
                // System.out.println(headers); 输出头字段

                //4.3获取响应正文
                BufferedReader  reader = null;
                StringBuffer resultBuffer = new StringBuffer();
                String tempLine = null;

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                }
                //System.out.println(resultBuffer);
                reader.close();
                // result = new String(resultBuffer.toString().getBytes("GBK"),"GBK");
                result = resultBuffer.toString();
            } else {
                result = "定位失败";
            }
            return result;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            // e.printStackTrace();
            return "Error";
        }
    }
}
