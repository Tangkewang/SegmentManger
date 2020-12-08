package com.TKW.Test.MinioTaskPoll;

import com.TKW.Minio.MinioUtil;
import com.TKW.Utils.FileViewer;
import com.TKW.Utils.GsonUtils;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MinioTaskPoll {
    public static void main(String[] args) {
        String ip ="162...170";
        String accKey ="";
        String serKey = "";

        MinioUtil minioUtil = new MinioUtil(ip,accKey,serKey);

        String m3u8RootPath ="C:\\Users\\NING MEI\\Desktop\\FFmpeg切片测试";//m3u8文件的跟路径

        String filmId = "hawhauha";
        List<String> listFiles = FileViewer.getListFiles(m3u8RootPath, "", true);

        System.out.println(GsonUtils.toJson(listFiles));

        startPoll(listFiles,minioUtil,filmId);

    }
    public static void startPoll(List<String> list, MinioUtil minioUtil,String filmId){
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
//                    list.remove(list.get(i));

            System.out.println(threadName+"处理了"+subList.size()+"条！startIndex:"+startIndex+"|endIndex:"+endIndex);
            long endTime = System.currentTimeMillis();    //获取结束时间

            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        }
    }


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
