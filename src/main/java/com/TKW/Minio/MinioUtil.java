package com.TKW.Minio;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioUtil {
    private String ACCESS_KEY ;
    private String SECRET_KEY;
    private String END_POINT;

    private static Logger log = LoggerFactory.getLogger(MinioUtil.class);
    private  static MinioClient minioclient;

    public MinioUtil(String END_POINT,String ACCESS_KEY, String SECRET_KEY) {
        try {
            minioclient =new MinioClient(END_POINT,ACCESS_KEY,SECRET_KEY);
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    /**
     *  minio 桶上传
     * @param packageName  上传的 桶名字
     * @param fileName     上传的 文件名
     * @param suffix       后缀
     * @param fileUrl      文件路径
     */
    public static void push(String packageName, String fileName , String suffix, String fileUrl) {
        try {
            boolean isExist =minioclient.bucketExists(packageName);
            if (isExist){
                log.info(packageName+"已经存在了");
            }else {
                minioclient.makeBucket(packageName);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        try {
            minioclient.putObject(packageName,fileName+"."+suffix,fileUrl);
        } catch (InvalidBucketNameException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            //  e.printStackTrace();
        } catch (InsufficientDataException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (IOException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (NoResponseException e) {
            log.error(e.getMessage());
            //  e.printStackTrace();
        } catch (XmlPullParserException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (ErrorResponseException e) {
            log.error(e.getMessage());
            //  e.printStackTrace();
        } catch (InternalException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        } catch (InvalidArgumentException e) {
            log.error(e.getMessage());
            // e.printStackTrace();
        }
        log.info(fileUrl+fileName+"."+suffix+"插入成功");
    }

}
