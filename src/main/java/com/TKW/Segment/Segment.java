package com.TKW.Segment;

import com.TKW.Utils.GsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.TKW.Utils.FileViewer.*;

public class Segment {

    private static int SEGMENT_NUM = 10;

    /**
     * 进行切片任务
     * @param filmId      电影对应的 id值
     * @param btName      状态表中 种子对应的名字（文件夹或者是mkv文件）
     * @param power       要求切的分辨率 （3：720P  ，2：480P   ，1：320P）
     * @param subtitleUrl 字幕文件的地址
     * @return
     */
    public static List<String> segmentCmd(String filmId, String btName, int power, String subtitleUrl) {
        List<String>  messageList  = new ArrayList<>();

        String s_Power = "";
        String segmentPath = "";


        String m3u8Path = "";
        String rootPath = "/root/Downloads/" + filmId + "/";
        switch (power) {
            case 3:
                s_Power = ResolvingPower.CHAOQING_RESLOVING_POWER;
                segmentPath = rootPath + ResolvingPower.CHAOQING;
                m3u8Path = segmentPath + "/" + filmId + "-" + ResolvingPower.CHAOQING_NAME;
                break;
            case 2:
                s_Power = ResolvingPower.GAOQING_RESLOVING_POWER;
                segmentPath = rootPath + ResolvingPower.GAOQINIG;
                m3u8Path = segmentPath + "/" + filmId + "-" + ResolvingPower.GAPOQING_NAME;
                break;
            case 1:
                s_Power = ResolvingPower.BIAOQING_RESLOVING_POWER;
                segmentPath = rootPath + ResolvingPower.BIAOQING;
                m3u8Path = segmentPath + "/" + filmId + "-" + ResolvingPower.BIAOQING_NAME;
                break;
        }
        String m3u8Name = m3u8Path + ".m3u8";
        String tsName = m3u8Path + "-%03d.ts";

        System.out.println("需要创建的文件夹为："+segmentPath);
        File file = new File(segmentPath);

        if (!file.exists()) {
            file.mkdirs();
        }

//        String firstFilmUrl = btOrgin(btName).get(0);

        String firstFilmUrl="/root/Downloads/Jade.Come.Home.Love：Lo.And.Behold.Ep1075.HDTV.720p.H264-CNHK/Jade.Come.Home.Love：Lo.And.Behold.Ep1075.HDTV.720p.H264-CNHK.ts";
        //System.out.println(GsonUtils.toJson(btOrgin(btName)));

        int i = firstFilmUrl.lastIndexOf("/");
        String orginRootPath = firstFilmUrl.substring(0, i);

        String orginName = firstFilmUrl.substring(i + 1);

        List<String> cmd = new ArrayList<>();
//        cmd.add("sudo");
//        cmd.add("-p");
//        cmd.add("Phliyundi888999");
//        cmd.add("bash");
//        cmd.add("-c");
        cmd.add("cd");
        cmd.add(orginRootPath);
        cmd.add(";");
        cmd.add("ffmpeg");
        cmd.add("-i");
        cmd.add("\""+  orginName + "\"");


        if (!subtitleUrl.equals("")) {
            cmd.add("-vf");
            cmd.add("subtitles=");
            cmd.add(subtitleUrl);
        }
        cmd.add("-s");
        cmd.add(s_Power);

        cmd.add("-b:v");
        cmd.add("200000k");

        cmd.add("-f");
        cmd.add("segment");
        cmd.add("-segment_list");
        cmd.add(m3u8Name);

        cmd.add("-segment_time");
        cmd.add(String.valueOf(SEGMENT_NUM));

        cmd.add(tsName);

        String execCmd = "";
        for (int j = 0; j < cmd.size(); j++) {
            execCmd += cmd.get(j) + " ";
        }
        messageList.add(execCmd);
        messageList.add(m3u8Name);
        return messageList;
    }


    /**
     * 获取 下载后的 所有文件的 种子文件（mkv）
     * @param name  种子下载的名字
     * @return
     */
    private static List<String> btOrgin(String name) {
        String rootPath = "/root/Downloads/";
        String btOrginPath = "";
        List<String> btFileList = new ArrayList<>();
        if (name.contains(".mkv") || name.contains(".ts") || name.contains(".mp4")) {
            btOrginPath = rootPath +"\""+ name + "\"";
            btFileList.add(btOrginPath);
        } else {
            List<String> list = getListFiles(rootPath +"\""+ name+"\"", "", true);
            for (String fileName : list) {
                if (fileName.contains(".mkv")||fileName.contains(".ts")||fileName.contains(".mp4")){
                    int i = fileName.lastIndexOf("/");
                    String fileRootPath =fileName.substring(0,i);
                    String nameSuffix = fileName.substring(i + 1);
                    int i1 = nameSuffix.lastIndexOf(".");
                    String filmName = nameSuffix.substring(0, i1);
                    String fileNameSuffix = nameSuffix.substring(i1+1);


                    btFileList.add(fileRootPath+"/"+"\""+filmName+"\""+"."+fileNameSuffix);
                }
            }
            return btFileList;
        }
        return null;
    }
}
