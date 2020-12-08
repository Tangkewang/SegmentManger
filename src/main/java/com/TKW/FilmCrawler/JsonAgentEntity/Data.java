package com.TKW.FilmCrawler.JsonAgentEntity;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@lombok.Data
public class Data {

    private int count;

    @SerializedName("proxy_list")
    private List<String> proxyList;

    @SerializedName("today_left_count")
    private int todayLeftCount;

    @SerializedName("dedup_count")
    private int dedupCount;

}
