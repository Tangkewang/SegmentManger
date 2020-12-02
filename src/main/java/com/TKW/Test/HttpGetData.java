package com.TKW.Test;

import okhttp3.*;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class HttpGetData {
    private  static  String  cooki="";
    private  static  String  cooki2="";
    private  static  String  execution="";
    public static String getHtml(String path) throws Exception {
        OkHttpClient client = new OkHttpClient();
        // 创建一个请求
        Request request = new Request.Builder()
                .url(path)
                .build();
        //给okhttpClient传入一个请求，这个时候得到一个call
        Call call = client.newCall(request);
        Response response = call.execute();
        int code = response.code();
        if (code == 200) {
            ResponseBody body = response.body();
            //获取头信息
            Headers  headers=response.headers();
            //获取cookie
            List<String> cookies=headers.values("Set-Cookie");

            String a[] = cookies.toString().split(";");
            //保存cookie数据
            cooki=a[0].substring(12);
            byte[] bytes = body.bytes();
            // byte[] data = StreamTool.read(response);

            String html = new String(bytes, "UTF-8").trim().replace("\"","");
            execution= StringUtils.substringBetween(html,"execution value=","/>");
            System.out.println(StringUtils.substringBetween(html,"execution value=","/>"));

            return html;
        }
        return "500";
    }}
