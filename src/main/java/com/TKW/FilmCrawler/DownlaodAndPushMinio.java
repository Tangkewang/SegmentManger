package com.TKW.FilmCrawler;

import com.TKW.Utils.HttpRequest;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DownlaodAndPushMinio {

    /**
     * 操作上传并 删除
     * @param url   网络请求 的图片路径
     * @param doubanId   http返回的 豆瓣 id
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws InvalidArgumentException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     */
    public static void start(String url,String doubanId) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidPortException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InvalidEndpointException, InternalException {
        String savePath = HttpRequest.downLoadFromUrl(url, doubanId + ".jpg", "/root/filmPicDownload/");

        PicMinio.picUpload(savePath,doubanId);

        File file = new File(savePath);

        if (file.exists()){
            file.delete();
        }
    }

//    public static void main(String[] args) {
//        String url = "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2627076341.webp";
//
//        int doubanId = 0 ;
//
//        try {
//            String savePath = HttpRequest.downLoadFromUrl(url, doubanId + ".jpg", "/root/filmPicDownload/");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
