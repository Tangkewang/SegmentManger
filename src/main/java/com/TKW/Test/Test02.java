package com.TKW.Test;

import com.TKW.Utils.GsonUtils;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

public class Test02 {

    private  static  String  cooki="";
    private  static  String  cooki2="";
    private  static  String  execution="";
    public static void main(String[] args) throws IOException {
        String url = "https://pt.m-team.cc/takelogin.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
//                .add("username", "speedking")
//                .add("password","EDHD3F6E")
                .build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        int code = response.code();


            Headers headers =response.headers();

            List<String> cookies=headers.values("Set-Cookie");

            String a[] = cookies.toString().split(";");


         //  System.out.println(GsonUtils.toJson(a));
            //保存cookie数据
            cooki=a[0];

//            Headers requestHeader = request.headers();
//
//            System.out.println(GsonUtils.toJson(requestHeader));
//            List<String> requestCookies = requestHeader.values("cookie");
//
//
//            System.out.println("reqCookie++++++"+requestCookies);



        System.out.println(cooki);
    }
}
