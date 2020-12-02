package com.TKW.Test;

import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Test01 {
    public static void main(String[] args) throws Exception {

        String url  ="https://pt.m-team.cc/takelogin.php";
        Map<String,String> params = new HashMap<>();
        params.put("username","speedking");
        params.put("password","EDHD3F6E");

        sendPostRequest(url,params,"UTF-8");
    }
    public static boolean sendPostRequest(String path, Map<String, String> params, String enc) throws Exception{
        // title=dsfdsf&timelength=23&method=save
        StringBuilder sb = new StringBuilder();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                sb.append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(entry.getValue(), enc)).append('&');
            }
            sb.deleteCharAt(sb.length()-1);
        }
        byte[] entitydata = sb.toString().getBytes();//得到实体的二进制数据
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5 * 1000);
        conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
        //Content-Type: application/x-www-form-urlencoded
        //Content-Length: 38
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));

        OutputStream outStream = conn.getOutputStream();
        outStream.write(entitydata);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode()==200){
            return true;
        }
        return false;
    }
}
