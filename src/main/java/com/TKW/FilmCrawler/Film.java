package com.TKW.FilmCrawler;


import lombok.Data;

import java.util.List;

@Data
public class Film {
    private int id;

    private String chineseName;//中文名

    private String englishName;//英文名

    private String director;//导演

    private String author;//编剧

    private String actor;//演员

    private String mold;//类型

    private String productionCountry;//制片国家

    private String moreLanguage;//语言

    private String datePublished;//上映日期

    private String Premiere;//首播

    private int episodeNumber;//集数

    private String singleLength;//单集片长

    private String otherName;//别名

    private String officialWebsite;//官方网站

    private int doubanId;//豆瓣id

    private String imdbId;//imdb编号

    private String tag;//标签

    private List<String> fileImages;//影片图片集

    private String filmCoverImage;//电影封面

    private String description;//内容简介

    private List<String> singleIntroduction;//每集简介

    private String ratingCount;//评分人数

    private double ratingValue;//豆瓣评分

    private String filmUrl;//电影的路径


    private Integer film_url_id = 0;


    private String filmYear;

    private int category;//对于电影电视剧的判断

}
