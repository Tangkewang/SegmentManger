package com.TKW.Utils;

import com.TKW.Data.HttpBackMessage.HttpBackMessage;
import com.TKW.Data.HttpReceiveMess.SegmentManager;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private static String ip ="http://192.168.50.5:8803/deal/";

    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");

    public static void main(String[] args) throws IOException {
//
        SegmentManager segmentManager = taskServer();
        System.out.println(GsonUtils.toJson(segmentManager));
//        HttpBackMessage httpBackMessage = new HttpBackMessage();
//
//        httpBackMessage.setData("dasd");
//        httpBackMessage.setCode(1);
//        httpBackMessage.setFilmId("dqwdqwdqwd");
//        updateState(httpBackMessage);

    }

    /**
     * 请求 任务接口
     * @return segmentManager类
     * @throws IOException
     */
    public static SegmentManager taskServer() throws IOException {
        String url = ip +"distributionTask";


        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        String html = response.body().string();
        SegmentManager segmentManager = GsonUtils.fromJson(html, SegmentManager.class);
        return segmentManager;
    }

    /**
     * 更新 状态给服务器
     * @param httpBackMessage
     * @return
     * @throws IOException
     */
    public static String updateState(HttpBackMessage httpBackMessage) {
        try {
            String url = ip +"taskState";
            System.out.println(url);
            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("content-type","application/json;charset:utf-8")
                    .post(RequestBody.create(JSON,GsonUtils.toJson(httpBackMessage)))
                    .build();


            Response response = okHttpClient.newCall(request).execute();

            String html = response.body().string();
            return html;

        }catch (Exception e){

        }
      return null;
    }
}
