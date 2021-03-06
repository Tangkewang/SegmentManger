package com.TKW.Minio;


import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MinioUtil {

    private static Logger log = LoggerFactory.getLogger(MinioUtil.class);
    private  static MinioClient minioclient;

    public MinioUtil(String END_POINT,String ACCESS_KEY, String SECRET_KEY) {
        String ip = "http://"+ END_POINT+":9000";
        try {
            minioclient = new MinioClient(ip,ACCESS_KEY,SECRET_KEY);
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
    public void push(String packageName, String fileName, String suffix, String fileUrl) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
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
        minioclient.putObject(packageName,fileName+"."+suffix,fileUrl);

        log.info(fileUrl+fileName+"."+suffix+"插入成功");
    }

    /**
     * 根据 list数目 分配 任务
     * @param list         文件夹下所有文件的全路径list
     * @param minioUtil    初始化连接一次的 minioutil （避免重复连接）
     * @param filmId       指定 minio 桶下的 文件夹名字
     */
    public static void startPoll(List<String> list, MinioUtil minioUtil, String filmId){
        int length = list.size();
        int num = 5; //初始线程数

        //启动多线程
        if(num > length){
            num = length;
        }
        int baseNum = length / num;
        int remainderNum = length % num;
        int end  = 0;
        for (int i = 0; i < num; i++) {
            int start = end ;
            end = start + baseNum;
            if(i == (num-1)){
                end = length;
            }else if( i < remainderNum){
                end = end + 1;
            }
            HandleThread thread = new HandleThread("线程[" + (i + 1) + "] ",  list, start , end,minioUtil,filmId);
            thread.start();
        }

    }

    /**
     * 任务处理类
     */
    public static class HandleThread extends Thread {
        private String threadName;
        private List<String> list;
        private int startIndex;
        private int endIndex;
        private MinioUtil minioUtil;
        private String filmId;

        public HandleThread(String threadName, List<String> list, int startIndex, int endIndex,MinioUtil minioUtil,String filmId) {
            this.threadName = threadName;
            this.list = list;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.minioUtil =minioUtil;
            this.filmId =filmId;
        }
        public void run() {
            long startTime = System.currentTimeMillis();    //获取开始时间

            List<String> subList = list.subList(startIndex, endIndex);
            for (int i = startIndex; i < endIndex; i++) {
                try {
                    minioUpload(list.get(i),minioUtil,filmId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(threadName+"处理了"+subList.size()+"条！startIndex:"+startIndex+"|endIndex:"+endIndex);
            long endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        }
    }

    /**
     *  根据 一个文件开启 一个上传认为
     * @param file         文件的路径
     * @param minioUtil    初始化的 minoiuitl地址
     * @param filmId       桶下的 文件夹名字
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidArgumentException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InternalException
     */
    public static void minioUpload(String file,MinioUtil minioUtil,String filmId) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        String spit  ="";
        if (file.contains("/")){
            spit ="/";
        }else if (file.contains("\\")){
            spit = "\\";
        }
        String nameSuffix = file.substring(file.lastIndexOf(spit) + 1);
        String name = nameSuffix.substring(0, nameSuffix.lastIndexOf("."));
        String suffix = nameSuffix.substring(nameSuffix.lastIndexOf(".") + 1);
        System.out.println("开始上传+" + name);
        minioUtil.push("film", filmId + "/" + name, suffix, file);
    }

}
