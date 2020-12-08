package com.TKW.Data.HttpBackMessage;

import lombok.Data;

@Data
public class MinioBackMessage {
    /**
     * 原本的大小
     */
    private String originalSize;

    /**
     * 实际使用的 大小
     */
    private String actualSize;

    /**
     * 上传 桶的路径
     */
    private String url;

}
