package com.TKW.FilmCrawler;


import lombok.Data;

@Data
public class FilmDrama {


    private int id ;//自增长得id

    private int num;

    private String description;//单个电视剧的描述

    private String doubanId; //豆瓣id

    private int filmUrlId; //传过来的自己的id

}
