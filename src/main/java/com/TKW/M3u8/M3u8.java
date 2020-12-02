package com.TKW.M3u8;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class M3u8 {


    public static void main(String[] args) {
        String filePath = "C:\\Users\\NING MEI\\Desktop\\m3u8OverTest\\test.m3u8";

        String rootPath = "C:\\Users\\NING MEI\\Desktop\\m3u8OverTest\\";


//        String s = readFileContent(filePath);
//       // System.out.println(s);
//        String strings = getNewM3u8Test(rootPath, s);
//        System.out.println(strings);
//        newM3U8(filePath);
        boolean b = m3u8FileOver(filePath);
        System.out.println(b);
    }

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

    public static void replace(String fileName,String mess)
    {
        try
        {
            BufferedWriter out=new BufferedWriter(new FileWriter(fileName));
//            out.write("Hello Kuka:");
//            out.newLine();  //注意\n不一定在各种计算机上都能产生换行的效果
//            out.write("  My name is coolszy!\n");
//            out.write("  I like you and miss you。");
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
                String tsPath = rootPath+ "\\"+ split[i];
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

    private static long fileSize(String filePath){
        File file = new File(filePath);
        long length = file.length();
        return length;
    }


    /**
     * 判断 m3u8文件是否 切片完成
     * @param m3u8Url
     * @return
     */
    public static boolean m3u8FileOver(String m3u8Url){
       int num =0;
       String spit ="";
       if (m3u8Url.contains("/")){
           num = m3u8Url.lastIndexOf("/");
           spit= "/";
       }else if (m3u8Url.contains("\\")){
           num = m3u8Url.lastIndexOf("\\");
           spit= "\\";
       }

       String rootPath = m3u8Url.substring(0,num);

        String s = readFileContent(m3u8Url);
        String[] split = s.split("\n");
        List<String> tsList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(".ts")){
                tsList.add(split[i]);
            }
        }
        String lastTsName = tsList.get(tsList.size() - 1);
        String lastTsPath = rootPath+ spit+lastTsName;

        System.out.println("最后一个ts 的路径玮："+lastTsPath);
        File file = new File(lastTsPath);
        if (file.exists()){
            return true;
        }else {
            return false;
        }
    }
}

