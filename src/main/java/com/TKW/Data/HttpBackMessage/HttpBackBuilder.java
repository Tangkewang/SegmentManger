package com.TKW.Data.HttpBackMessage;
import com.TKW.Utils.GsonUtils;


/**
 * http 返回的 消息体的 构造类
 */
public class HttpBackBuilder {

//    public static String buildBtDownload(String filmId, BitTorrent bitTorrent){
//        HttpBackMessage backMessage = new HttpBackMessage();
//        backMessage.setCode(2);
//        backMessage.setData(bitTorrent.toString());
//        backMessage.setFilmId(filmId);
//
//        return GsonUtils.toJson(backMessage);
//    }

    /**
     * bt 下载状态返回的 状态码
     * @param filmId
     * @param bitTorrent
     * @return
     */
    public static String buildBtDownload(String filmId, BitTorrent bitTorrent){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2);
        backMessage.setData(bitTorrent.toString());
        backMessage.setFilmId(filmId);

        return GsonUtils.toJson(backMessage);
    }

    /**
     *  进行 720p切片返回状态
     * @param filmId
     * @return
     */
    public static String build720Segment(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(3);
        backMessage.setData("OK");
        backMessage.setFilmId(filmId);

        return GsonUtils.toJson(backMessage);

    }

    /**
     * 进行 480p切片返回的状态
     * @param filmId
     * @return
     */
    public static String build480Segment(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(4);
        backMessage.setData("OK");
        backMessage.setFilmId(filmId);

        return GsonUtils.toJson(backMessage);

    }

    /**
     * 进行 320p返回的状态
     * @param filmId
     * @return
     */
    public static String build320Segment(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(5);
        backMessage.setData("OK");
        backMessage.setFilmId(filmId);

        return GsonUtils.toJson(backMessage);

    }

    /**
     * 返回 minio 上传的状态
     * @param filmId 电影 id
     * @param url
     * @param power
     * @return
     */
    public static String minioUpload(String filmId,String url,String power){
        HttpBackMessage backMessage = new HttpBackMessage();

        backMessage.setData(url);
        backMessage.setFilmId(filmId);
        if (power.contains("720P")){
            backMessage.setCode(63);
        }else if (power.contains("480P")){
            backMessage.setCode(64);
        }else if (power.contains("320P")){
            backMessage.setCode(65);
        }
        return GsonUtils.toJson(backMessage);
    }

}