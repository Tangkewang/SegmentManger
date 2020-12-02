package com.TKW.Main;

import com.TKW.Data.HttpBackMessage.BitTorrent;
import com.TKW.Data.HttpReceiveMess.MinioServer;
import com.TKW.Data.HttpReceiveMess.SegmentManager;
import com.TKW.M3u8.M3u8;
import com.TKW.Minio.MinioUtil;
import com.TKW.Segment.ResolvingPower;
import com.TKW.Segment.Segment;
import com.TKW.Ssh.RemoteExecuteCommand;
import com.TKW.Utils.*;
import net.sf.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static String ip ="62.210.249.200";
    private static String userName ="root";
    private static String userPwd ="Phliyundi888999";
    private static Map<String,SegmentManager> taskMap = new ConcurrentHashMap<>();

    private static List<String> filmList = new ArrayList<>();
    /**
     * 电影id 和 bt种子名字
     */
    private static Map<String,String> filmIdBtNameMap =new ConcurrentHashMap<>();

    private static   RemoteExecuteCommand remoteExecuteCommand = new RemoteExecuteCommand(ip,userName,userPwd);
    public static void main(String[] args) throws Exception {
//        while (true){
//            if (taskMap.size()<=5){
//            }
//        }

        seekTaskFromHttpServer();


        while (true){
            updateState();
        }
    }

    /**
     * 服务器请求 任务接口
     * @return
     */
    private static boolean seekTaskFromHttpServer() throws InterruptedException {
//        SegmentManager segmentManager=null;
//        try {
//             segmentManager = HttpServer.taskServer();
//            System.out.println("收到的数据："+segmentManager);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String mess ="{\"filmId\":\"mt9hdCAjlm\",\"btUrl\":\"https://pt.m-team.cc/download.php?id\\u003d444860\\u0026passkey\\u003d9f1e9849aa4d5eb1daa7f313c47c794b\\u0026ipv6\\u003d1\\u0026https\\u003d1\",\"subtitleUrl\":\"\",\"resolvingPower\":\"720P\",\"msg\":{\"720P\":\"[{ip\\u003d195.154.166.87, accessKey\\u003d3d43bd239bc5c750af5c4174600633f4, secretKey\\u003d5a09133825573ac6aa40a0a7e1a45d07}, {ip\\u003d62.210.199.67, accessKey\\u003d3d43bd239bc5c750af5c4174600633f4, secretKey\\u003d5a09133825573ac6aa40a0a7e1a45d07}]\"},\"subtitleSuffix\":\"srt\"}\n";
        SegmentManager segmentManager = GsonUtils.fromJson(mess, SegmentManager.class);

        System.out.println(segmentManager.getBtUrl());
        if (segmentManager!=null){
            String btUrl = segmentManager.getBtUrl();
            String filmId = segmentManager.getFilmId();
            String btDownloadName = null;
            try {
                for (int i = 0; i < 3; i++) {
                    btDownloadName = BDecoder.start(btUrl);
                    if (btDownloadName!=null){
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("解析 bturl 种子文件名字错误"+e.getMessage());
            }

            taskMap.put(filmId,segmentManager);
            filmList.add(filmId);
            filmIdBtNameMap.put(filmId,btDownloadName);

            if ( remoteExecuteCommand.startBtCmd(btUrl)){

                return true;
            }
        }

        return false;
    }

    /**
     * 实时更新状态 并判断bt下载状态 进行切片，上传
     */
    private static void updateState() throws IOException, InterruptedException {
        //先更新命令行获取的状态
        Map<String, BitTorrent> bitTorrentMap = remoteExecuteCommand.btDownState();

        //遍历任务列表
        for (int i = 0; i < filmList.size(); i++) {
            String s = filmList.get(i);
            String btName = filmIdBtNameMap.get(s);
            BitTorrent bitTorrent = bitTorrentMap.get(btName);

            if (bitTorrent.getDone().contains("100%")){
                System.out.println("开始执行:"+bitTorrent.getName()+bitTorrent.getDone());
                SegmentManager segmentManager = taskMap.get(filmList.get(i));


                String subtitleUrl = segmentManager.getSubtitleUrl();

                String resolvingPower = segmentManager.getResolvingPower();
                String[] split = resolvingPower.split(",");
                for (int j = 0; j < split.length; j++) {
                    if (split[j].contains("720P")){
                        doSegment(filmList.get(i),btName,3,subtitleUrl);
                    }else if(split[j].contains("480P")){
                        doSegment(filmList.get(i), btName, 2, subtitleUrl);
                    }else if (split[j].contains("320P")){
                        doSegment(filmList.get(i), btName, 1, subtitleUrl);
                    }
                }
            }else {

                System.out.println(bitTorrent.getName()+"还在下载中"+bitTorrent.getDone());

                Thread.sleep(5000);
            }
        }

    }

    /**
     * 进行 切片任务 并上传到桶服务器中
     * @param filmId            电影id
     * @param btName            种子名字
     * @param power             分辨率
     * @param subtitleUrl       字幕文件地址
     */
    public static void doSegment(String filmId, String btName, int power, String subtitleUrl){
        String resolvingPower ="";
        switch (power){
            case 3:
                resolvingPower = ResolvingPower.CHAOQING;
                break;
            case 2:
                resolvingPower = ResolvingPower.GAOQINIG;
                break;
            case 1:
                resolvingPower =ResolvingPower.BIAOQING;
                break;
        }

        List<String> list = Segment.segmentCmd(filmId, btName, 3, subtitleUrl);
        String cmd = list.get(0);
        System.out.println(cmd);

//        CommandUtil.run(cmd);
        String result = remoteExecuteCommand.execute(cmd);
        if (result != null) {
            System.out.println("当前res 输出的 值为"+result);
            String m3u8Path = list.get(1);
            File file = new File(m3u8Path);
            if (file.exists()){
                if (M3u8.m3u8FileOver(m3u8Path)){
                    //传入m3u8文件的 全路径 生成新的 m3u8文件
                    M3u8.newM3U8(m3u8Path);
                }
            }
        }

      //  remoteExecuteCommand.execute(cmd);



//        List<MinioServer> info = getInfo(resolvingPower);
//
//        MinioServer minioServer = info.get(0);
//        String ip = minioServer.getIp();
//        String accessKey = minioServer.getAccessKey();
//        String secretKey = minioServer.getSecretKey();

//        List<String> listFiles = FileViewer.getListFiles(m3u8Path,"", true);
//        MinioUtil minioUtil = new MinioUtil(ip,accessKey,secretKey);
//        for (String file : listFiles){
//            int nameIndex = file.lastIndexOf("/");
//            String nameSuffix = file.substring(nameIndex+1);
//            int suffixIndex = nameSuffix.lastIndexOf(".");
//            String name = nameSuffix.substring(0,suffixIndex);
//            String suffix  = file.substring(suffixIndex+1);
//
//            minioUtil.push("film",filmId+"/"+name,suffix,file);
//        }

    }

    /**
     * 解析 info 中的 桶的
     *
     * @param info
     * @return
     */
    private static List<MinioServer> getInfo(String info) {
        List<MinioServer> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(info);

        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(GsonUtils.fromJson(String.valueOf(jsonArray.get(i)), MinioServer.class));
        }
        return list;
    }



}
