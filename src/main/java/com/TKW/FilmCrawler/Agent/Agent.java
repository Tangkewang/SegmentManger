package com.TKW.FilmCrawler.Agent;



import com.TKW.FilmCrawler.JsonAgentEntity.Data;
import com.TKW.FilmCrawler.JsonAgentEntity.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Agent {

    private static String apiUrl = "http://dps.kdlapi.com/api/getdps/?orderid=939056585568716&num=1&pt=1&format=json&sep=1"; //API链接


    public static  void main(String[] args){

        Agent agent = new Agent();
        getHostPort();
    }

    public static AgentUrl getHostPort () {
        HttpRequest request = new HttpRequest();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept-Encoding", "gzip"); //使用gzip压缩传输数据让访问更快
        try {
            HttpResponse response = null;
            try {
                response = request.sendGet(apiUrl, null, headers, null);
            } catch (IOException e) {
                e.printStackTrace();
            }


            String html = response.getContentCollection().toString();
            System.out.println(html);

            Gson gon = new Gson();
            List<Item> itemList = gon.fromJson(html, new TypeToken<List<Item>>(){}.getType());

            if(itemList != null) {
                Item item = itemList.get(0);
                //System.out.println(item.toString());
                Data data = item.getData();

                List<String> ipList = data.getProxyList();
                if (ipList != null) {

                    String ipInfo = ipList.get(0);
                    String[] values = ipInfo.split(":");

                    if (values != null && values.length == 2) {
                        String ip = values[0];
                        int port = Integer.parseInt(values[1]);

                        AgentUrl ret = new AgentUrl(ip, port);
                      //  System.out.println(ret.toString());
                        return ret;
                    }
                }

            }

            return null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}