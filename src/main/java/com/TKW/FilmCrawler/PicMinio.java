package com.TKW.FilmCrawler;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class PicMinio {
    private static String ACCESS_KEY = "3d43bd239bf5c4174600633f4";
    private static String SECRET_KET = "5a0913382a40a0a7e1a45d07";
    private static String END_POINT = "http://195.:9000";

    /**
     * 电影图片 上传到桶中
     * @param fileUrl
     * @param doubanId
     * @throws InvalidPortException
     * @throws InvalidEndpointException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InsufficientDataException
     * @throws InvalidArgumentException
     * @throws InternalException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws XmlPullParserException
     * @throws ErrorResponseException
     */
    public static void picUpload(String fileUrl,String doubanId) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        MinioClient minioClient = new MinioClient(END_POINT, ACCESS_KEY, SECRET_KET);
        minioClient.putObject("film-image", doubanId + ".jpg" , fileUrl);
    }
}
