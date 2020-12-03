package com.TKW.Data.HttpBackMessage;
import com.TKW.Utils.GsonUtils;
import com.google.gson.Gson;


/**
 * http 返回的 消息体的 构造类
 */
public class HttpBackBuilder {


    /**
     * 更新 种子 下载状态
     * @param filmId
     * @param bitTorrent
     * @return
     */
    public static HttpBackMessage buildBtState(String filmId,BitTorrent bitTorrent){
        HttpBackMessage httpBackMessage = new HttpBackMessage();

        httpBackMessage.setData(GsonUtils.toJson(bitTorrent));
        httpBackMessage.setCode(2);
        httpBackMessage.setFilmId(filmId);

        return httpBackMessage;

    }
    /**
     * 种子下载无效
     * @param filmId
     * @return
     */
    public static HttpBackMessage buildBtInvalid(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(1001);
        backMessage.setData("Invalid");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 字幕无效
     * @param filmId
     * @return
     */
    public static HttpBackMessage buildSubtitleInvalid(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(1002);
        backMessage.setData("Invalid");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 720 开始切片任务
     * @param filmId
     * @return
     */
    public static HttpBackMessage build720Start(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2001);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 720P 切片结束
     * @param filmId
     * @return
     */
    public static HttpBackMessage build720SegementDone(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2011);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 720P 切片失败
     * @param filmId
     * @return
     */
    public static HttpBackMessage build720Error(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2021);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }


    /**
     * 720 上传桶状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build720UploadState(String filmId, MinioBackMessage minioBackMessage){

        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(6003);
        backMessage.setFilmId(filmId);

        if (minioBackMessage !=null){
            backMessage.setData(GsonUtils.toJson(minioBackMessage));
        }else {
            backMessage.setData("");
        }

        return backMessage;
    }


    /**
     * 720 上传桶失败状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build720UploadError(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();

        backMessage.setCode(6023);
        backMessage.setData("Error");
        backMessage.setFilmId(filmId);

        return backMessage;
    }


    /**
     * 480 开始切片任务
     * @param filmId
     * @return
     */
    public static HttpBackMessage build480Start(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2002);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 480P 切片结束
     * @param filmId
     * @return
     */
    public static HttpBackMessage build480SegementDone(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2012);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 480P 切片失败
     * @param filmId
     * @return
     */
    public static HttpBackMessage build480Error(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2022);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 480 上传桶状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build480UploadState(String filmId,MinioBackMessage minioBackMessage){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(6004);
        backMessage.setFilmId(filmId);

        if (minioBackMessage !=null){
            backMessage.setData(GsonUtils.toJson(minioBackMessage));
        }else {
            backMessage.setData("");
        }


        return backMessage;
    }

    /**
     * 480 上传桶失败状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build480UploadError(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();

        backMessage.setCode(6024);
        backMessage.setData("Error");
        backMessage.setFilmId(filmId);

        return backMessage;
    }


    /**
     * 320 开始切片任务
     * @param filmId
     * @return
     */
    public static HttpBackMessage build320Start(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2003);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 320P 切片结束
     * @param filmId
     * @return
     */
    public static HttpBackMessage build320SegementDone(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2013);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 320P 切片失败
     * @param filmId
     * @return
     */
    public static HttpBackMessage build320Error(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(2023);
        backMessage.setData("Done");
        backMessage.setFilmId(filmId);

        return backMessage;
    }

    /**
     * 320 上传桶状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build320UploadState(String filmId,MinioBackMessage minioBackMessage){
        HttpBackMessage backMessage = new HttpBackMessage();
        backMessage.setCode(6005);

        backMessage.setFilmId(filmId);
        if (minioBackMessage !=null){
            backMessage.setData(GsonUtils.toJson(minioBackMessage));
        }else {
            backMessage.setData("");
        }
        return backMessage;
    }


    /**
     * 320 上传桶失败状态
     * @param filmId
     * @return
     */
    public static HttpBackMessage build320UploadError(String filmId){
        HttpBackMessage backMessage = new HttpBackMessage();

        backMessage.setCode(6025);
        backMessage.setData("Error");
        backMessage.setFilmId(filmId);

        return backMessage;
    }


    /**
     * 根据 分辨率 判断更新 上传状态
     * @param filmId
     * @param minioBackMessage
     * @param power
     * @return
     */
    public static HttpBackMessage buildUploadState(String filmId,MinioBackMessage minioBackMessage,int power){
        HttpBackMessage httpBackMessage = null;
        switch (power){
            case 3:
//                if (minioBackMessage!=null){
//
//                    break;;
//                }else {
//                    httpBackMessage=  build720UploadState(filmId,null);
//                }
                httpBackMessage = build720UploadState(filmId, minioBackMessage);
            case 2:
                httpBackMessage= build480UploadState(filmId,minioBackMessage);
                break;
            case 1:
                httpBackMessage=  build320UploadState(filmId,minioBackMessage);
                break;
        }
        return httpBackMessage;
    }

    /**
     * 上传 桶错误类
     * @param filmId
     * @param power
     * @return
     */
    public static HttpBackMessage buildUploadError(String filmId,int power){
        HttpBackMessage httpBackMessage = null;
        switch (power){
            case 3:
                httpBackMessage  = build720UploadError(filmId);
                break;
            case 2:
                httpBackMessage  = build480UploadError(filmId);
                break;
            case 1:
                httpBackMessage  = build320UploadError(filmId);
                break;
        }
        return httpBackMessage;
    }



}