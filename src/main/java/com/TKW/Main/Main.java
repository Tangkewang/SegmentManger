package com.TKW.Main;

import com.TKW.Data.HttpBackMessage.BitTorrent;
import com.TKW.Data.HttpBackMessage.HttpBackBuilder;
import com.TKW.Data.HttpBackMessage.HttpBackMessage;
import com.TKW.Data.HttpBackMessage.MinioBackMessage;
import com.TKW.Data.HttpReceiveMess.MinioServer;
import com.TKW.Data.HttpReceiveMess.SegmentManager;
import com.TKW.FilmCrawler.Crawler;
import com.TKW.FilmCrawler.DownlaodAndPushMinio;
import com.TKW.FilmCrawler.Film;
import com.TKW.M3u8.M3u8;
import com.TKW.Minio.MinioUtil;
import com.TKW.Segment.ResolvingPower;
import com.TKW.Segment.Segment;
import com.TKW.Ssh.RemoteExecuteCommand;
import com.TKW.Utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.TKW.Utils.FileViewer.getFileSize;

public class Main {

    private static String ip = "62.29.200";
    private static String userName = "root";
    private static String userPwd = "Phli8999";
    /**
     * 电影id 和 http请求的消息体
     */
    private static Map<String, SegmentManager> taskMap = new ConcurrentHashMap<>();

    private static List<String> filmList = new ArrayList<>();
    /**
     * 电影id 和 bt种子名字
     */
    private static Map<String, String> filmIdBtNameMap = new ConcurrentHashMap<>();

    private static RemoteExecuteCommand remoteExecuteCommand = new RemoteExecuteCommand(ip, userName, userPwd);

    public static void main(String[] args) throws Exception {
        /**
         * 判断任务列表 请求 开始新任务
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (taskMap.size() <= 5) {
                        try {
                            seekTaskFromHttpServer();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    } else {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

        /**
         * 判断任务列表 开始切片任务
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateState();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 服务器请求 任务接口
     *
     * @return
     */
    private static boolean seekTaskFromHttpServer() throws InterruptedException, IOException {
        SegmentManager segmentManager = null;
        try {
            segmentManager = HttpServer.taskServer();
            System.out.println("收到的数据：" + segmentManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String mess ="{\"filmId\":\"mt9hdCAjlm\",\"btUrl\":\"https://pt.m-team.cc/download.php?id\\u003d444860\\u0026passkey\\u003d9f1e9849aa4d5eb1daa7f313c47c794b\\u0026ipv6\\u003d1\\u0026https\\u003d1\",\"subtitleUrl\":\"\",\"resolvingPower\":\"720P\",\"msg\":{\"720P\":\"[{ip\\u003d162.245.236.170, accessKey\\u003dHSCBKASDKLKL, secretKey\\u003dASNXCJNASJnjksancascjnkjasc}]\"},\"subtitleSuffix\":\"srt\"}\n";
//        SegmentManager segmentManager = GsonUtils.fromJson(mess, SegmentManager.class);

        if (segmentManager != null) {
            String douBanId = segmentManager.getDouBanId();
            if (douBanId != null){
                Film film = Crawler.start(douBanId);

                String s = HttpServer.updateState(HttpBackBuilder.buildFilmBasicMess(film, segmentManager.getFilmId()));

                HttpBackMessage httpBackMessage = GsonUtils.fromJson(s, HttpBackMessage.class);
                String filmId = httpBackMessage.getFilmId();
                String filmCoverImage = film.getFilmCoverImage();

                try {
                    DownlaodAndPushMinio.start(filmCoverImage,filmId);
                }catch (Exception e){

                    System.out.println("下载上传 错误 +"+e.getMessage());
                }
            }

            String btUrl = segmentManager.getBtUrl();
            String filmId = segmentManager.getFilmId();
            String btDownloadName = null;
            try {
                for (int i = 0; i < 3; i++) {
                    btDownloadName = BDecoder.start(btUrl);
                    if (btDownloadName != null) {
                        break;
                    }
                }

            } catch (Exception e) {
                HttpServer.updateState(HttpBackBuilder.buildBtInvalid(filmId));
                System.out.println("解析 bturl 种子文件名字错误" + e.getMessage());
            }

            taskMap.put(filmId, segmentManager);

            filmList.add(filmId);
            filmIdBtNameMap.put(filmId, btDownloadName);
            if (remoteExecuteCommand.startBtCmd(btUrl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从 任务map 中将任务删除
     * @param filmId
     */
    private static void deleteTaskMap(String filmId) {
        taskMap.remove(filmId);
        filmList.remove(filmId);
        filmIdBtNameMap.remove(filmId);
    }

    /**
     * 实时更新状态 并判断bt下载状态 进行切片，上传
     */
    private static void updateState() throws InterruptedException, IOException {
        //先更新命令行获取的状态
        Map<String, BitTorrent> bitTorrentMap = remoteExecuteCommand.btDownState();

        //遍历任务列表
        for (int i = 0; i < filmList.size(); i++) {
            String filmId = filmList.get(i);
            String btName = filmIdBtNameMap.get(filmId);
            BitTorrent bitTorrent = bitTorrentMap.get(btName);

            //更新 种子下载状态 到服务器
            HttpServer.updateState(HttpBackBuilder.buildBtState(filmId, bitTorrent));

            if (bitTorrent.getDone().contains("100%")) {
                deleteTaskMap(filmId);
                System.out.println("开始执行:" + bitTorrent.getName() + bitTorrent.getDone());
                SegmentManager segmentManager = taskMap.get(filmList.get(i));


                String subtitleUrl = segmentManager.getSubtitleUrl();

                String resolvingPower = segmentManager.getResolvingPower();

                Map<String, String> msg = segmentManager.getMsg();


                String[] split = resolvingPower.split(",");
                for (int j = 0; j < split.length; j++) {
                    if (split[j].contains("720P")) {
                        doSegment(filmList.get(i), btName, 3, subtitleUrl, msg.get("720P"));
                    } else if (split[j].contains("480P")) {
                        doSegment(filmList.get(i), btName, 2, subtitleUrl, msg.get("480P"));
                    } else if (split[j].contains("320P")) {
                        doSegment(filmList.get(i), btName, 1, subtitleUrl, msg.get("320P"));
                    }
                }
            } else {

                System.out.println(bitTorrent.getName() + "还在下载中" + bitTorrent.getDone());

                Thread.sleep(5000);
            }
        }

    }

    /**
     * 进行 切片任务 并上传到桶服务器中
     *
     * @param filmId      电影id
     * @param btName      种子名字
     * @param power       分辨率
     * @param subtitleUrl 字幕文件地址
     */
    public static void doSegment(String filmId, String btName, int power, String subtitleUrl, String minioList) throws IOException {
        String resolvingPower = "";
        switch (power) {
            case 3:
                resolvingPower = ResolvingPower.CHAOQING;
                HttpServer.updateState(HttpBackBuilder.build720Start(filmId));
                break;
            case 2:
                resolvingPower = ResolvingPower.GAOQINIG;
                HttpServer.updateState(HttpBackBuilder.build480Start(filmId));
                break;
            case 1:
                resolvingPower = ResolvingPower.BIAOQING;
                HttpServer.updateState(HttpBackBuilder.build320Start(filmId));
                break;
        }

        List<String> list = Segment.segmentCmd(filmId, btName, power, subtitleUrl);

        String s = list.get(0);

        System.out.println("此处的 cmd为" + s);

        try {
            remoteExecuteCommand.localExecute(s);
        } catch (IOException e) {
            switch (power) {
                case 3:
                    HttpServer.updateState(HttpBackBuilder.build720Error(filmId));
                    break;
                case 2:
                    HttpServer.updateState(HttpBackBuilder.build480Error(filmId));
                    break;
                case 1:
                    HttpServer.updateState(HttpBackBuilder.build320Error(filmId));
                    break;
            }
            e.printStackTrace();
        }

        List<MinioServer> info = getInfo(minioList);
        MinioServer minioServer = info.get(0);
        String m3u8Path = list.get(1);

        dealM3u8(m3u8Path, minioServer, filmId, power);

    }

    /**
     * 根据 m3u8文件的全路径 上传所有的切片文件到 桶服务器中
     *
     * @param m3u8Path    m3u8 文件 的全路径
     * @param minioServer minio信息的类
     * @param filmId      电影id
     * @param power       需要上传的分辨率
     */
    public static void minioPush(String m3u8Path, MinioServer minioServer, String filmId, int power) {
        HttpServer.updateState(HttpBackBuilder.buildUploadState(filmId, null, power));

        String ip = minioServer.getIp();
        String accessKey = minioServer.getAccessKey();
        String secretKey = minioServer.getSecretKey();


        int i = m3u8Path.lastIndexOf("/");
        String m3u8RootPath = m3u8Path.substring(0, i);

        String m3u8Suffix = m3u8Path.substring(i + 1);
        List<String> listFiles = FileViewer.getListFiles(m3u8RootPath, "", true);
        MinioUtil minioUtil = new MinioUtil(ip, accessKey, secretKey);

        String allSize = null;
        try {
            allSize = FileViewer.FormetFileSize(getFileSize(new File(m3u8RootPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            for (String file : listFiles) {
//                int nameIndex = file.lastIndexOf("/");
//                String nameSuffix = file.substring(nameIndex + 1);
//                int suffixIndex = nameSuffix.lastIndexOf(".");
//                String name = nameSuffix.substring(0, suffixIndex);
//                String suffix = nameSuffix.substring(suffixIndex + 1);
//
//                System.out.println("开始上传+" + name);
//                minioUtil.push("film", filmId + "/" + name, suffix, file);
//            }
//        } catch (Exception e) {
//            HttpServer.updateState(HttpBackBuilder.buildUploadError(filmId, power));
//        }
        //线程处理 minio上传
        MinioUtil.startPoll(listFiles,minioUtil,filmId);

        MinioBackMessage minioBackMessage = new MinioBackMessage();

        minioBackMessage.setActualSize(allSize);
        minioBackMessage.setOriginalSize(taskMap.get(filmId).getFilmSize());
        minioBackMessage.setUrl("http://" + ip + "/film" + "/" + filmId + "/" + m3u8Suffix);

        HttpServer.updateState(HttpBackBuilder.buildUploadState(filmId, minioBackMessage, power));

    }

    /**
     * 判断 切片是否完成 进行 m3u8格式话 上传桶（minio）
     *
     * @param M3U8Path    m3u8 文件 的全路径
     * @param minioServer minio信息的类
     * @param filmId      电影id
     * @param power       需要上传的分辨率
     * @throws IOException
     */
    public static void dealM3u8(String M3U8Path, MinioServer minioServer, String filmId, int power) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                File file = new File(M3U8Path);
                while (true) {
                    if (file.exists()) {
                        if (M3u8.m3u8Over(M3U8Path)) {
                            switch (power) {
                                case 3:
                                    HttpServer.updateState(HttpBackBuilder.build720SegementDone(filmId));
                                    break;
                                case 2:
                                    HttpServer.updateState(HttpBackBuilder.build480SegementDone(filmId));
                                    break;
                                case 1:
                                    HttpServer.updateState(HttpBackBuilder.build320SegementDone(filmId));
                                    break;
                            }
                            M3u8.newM3U8(M3U8Path);
                            minioPush(M3U8Path, minioServer, filmId, power);
                            break;
                        } else {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 解析 info 中的 桶的
     *
     * @param info
     * @return
     */
    private static List<MinioServer> getInfo(String info) {
        Gson gson = new Gson();
        List<MinioServer> da = gson.fromJson(info, new TypeToken<List<MinioServer>>() {
        }.getType());
        return da;
    }

}
