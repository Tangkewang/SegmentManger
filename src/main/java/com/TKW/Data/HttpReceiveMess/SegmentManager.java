package com.TKW.Data.HttpReceiveMess;


import lombok.Data;
import java.util.Map;

/**
 * 请求 的任务接口 返回的数据
 */
@Data
public class SegmentManager {

    /**
     * 电影id
     */
    private String filmId;

    /**
     * 种子链接
     */
    private String btUrl;

    /**
     * 字幕文件地址
     */
    private String subtitleUrl;

    /**
     * 分辨率
     */
    private String resolvingPower;

    /**
     * 桶消息体的  map
     * key ：分辨率名称
     * value： server的list消息体
     */
    private Map<String, String> msg;

    /**
     * 字幕文件的后缀
     */
    private String subtitleSuffix;

    /**
     * 种子原视频文件的 大小
     */
    private String filmSize;

    /**
     * 电影 豆瓣 id值
     */
    private String douBanId;

}
