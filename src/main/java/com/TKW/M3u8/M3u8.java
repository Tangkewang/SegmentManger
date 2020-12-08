package com.TKW.M3u8;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class M3u8 {

    /**
     * 生成 新的 m3u8文件
     * @param M3u8Path
     */
    public static void newM3U8(String M3u8Path){
        int i = 0;
        if (M3u8Path.contains("/")){
             i = M3u8Path.lastIndexOf("/");
        }else if (M3u8Path.contains("\\")){
            i = M3u8Path.lastIndexOf("\\");
        }

        String m3u8RootPath = M3u8Path.substring(0, i);

        String s = readFileContent(M3u8Path);
        String strings  = getNewM3u8Test(m3u8RootPath, s);

        replace(M3u8Path,strings);
    }

    /**
     * 替换 文件内容
     * @param fileName   文件的路径
     * @param mess       需要替换的内容
     */
    public static void replace(String fileName,String mess)
    {
        try
        {
            BufferedWriter out=new BufferedWriter(new FileWriter(fileName));
            String[] split = mess.split("\n");
            for (int i = 0; i < split.length; i++) {
                out.write(split[i]);
                out.write("\n");
            }
            out.close();
        } catch (IOException e)
        {
//             TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    /**
     * 读取  文件夹中的 内容
     *
     * @param fileName
     * @return
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr + "\n");
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }



    /**
     * 获取新的 m3u8文件内容
     *
     * @param mess
     * @return
     */
    private static String getNewM3u8Test(String rootPath, String mess) {
        String newMess = "";
        List<String> newTsTime = new ArrayList<>();
        List<String> oldTsTime = new ArrayList<>();
        String[] split = mess.split("\n");
        int startTime = 0;
        int endTime = 0;

        long bStart =0;
        long bEnd = 0;

        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("#EXTINF")) {
                oldTsTime.add(split[i]);
                String replace = split[i].replace("#EXTINF:", "").replace(",", "");

                Double d = Double.valueOf(replace);
                String format = String.format("%.3f", d);

                split[i] = "#EXTINF:" + format + ",";
                endTime = Integer.valueOf((int) (d*1000));
                newTsTime.add(format);
            } else if (split[i].contains(".ts")) {
                String tsPath = "";
                if (rootPath.contains("/")){
                    tsPath=  rootPath+ "/"+ split[i];
                }else if (rootPath.contains("\\")){
                    tsPath = rootPath+"\\" +split[i];
                }

                bEnd = startTime+ fileSize(tsPath)+bEnd;
                endTime= startTime+endTime;
                String tsList = split[i];
                split[i] = tsList + "?" + M3u8KeyWords.index + (newTsTime.size() - 1)+M3u8KeyWords.start+startTime+M3u8KeyWords.end+endTime+M3u8KeyWords.bStart+bStart+M3u8KeyWords.bEnd+bEnd+M3u8KeyWords.version+M3u8KeyWords.versionNum;
                startTime = endTime;
                bStart = bEnd+1;
            }
        }

        for (int i = 0; i < split.length; i++) {
            newMess+=split[i]+"\n";
        }

        return newMess;
    }

    /**
     * 读取 文件 大小 转成 long类型
     * @param filePath  文件 路径
     * @return
     */
    private static long fileSize(String filePath){
        File file = new File(filePath);
        long length = file.length();
        return length;
    }





    /**
     * 判断 m3u8文件是否 切片完成
     * @param m3u8Url  m3u8文件的 路径
     * @return
     */
    public static boolean m3u8Over(String m3u8Url){
        String s = readFileContent(m3u8Url);
        String[] split = s.split("\n");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("#EXT-X-ENDLIST")){
                return true;
            }
        }
        return  false;
    }

//    public static void main(String[] args) {
////        String filePath = "C:\\Users\\NING MEI\\Desktop\\m3u8OverTest\\test.m3u8";
////
////        String rootPath = "C:\\Users\\NING MEI\\Desktop\\m3u8OverTest\\";
////
////
////        String s = readFileContent(filePath);
////       // System.out.println(s);
////        String strings = getNewM3u8Test(rootPath, s);
////        System.out.println(strings);
//        String path = "/root/Downloads/mt9hdCAjlm/720P/mt9hdCAjlm-323001.m3u8";
//        newM3U8(path);
//
//    }
}

