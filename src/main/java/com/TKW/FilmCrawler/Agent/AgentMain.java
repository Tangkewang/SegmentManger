//package com.TKW.FilmCrawler.Agent;
//
//import Jsoup.File.FilmMessage;
//import Jsoup.File.FilmUrl;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import mapper.FilmUrlMapper;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.log4j.Logger;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import utils.MybatisUtil;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import static Agent.Agent.getHostPort;
//
//
///**
// * 调用豆瓣接口获取电影列表的数组
// */
//public class AgentMain {
//    private static MybatisUtil mybatisUtil = new MybatisUtil();
//    private static String apiUrl = "http://dps.kdlapi.com/api/getdps/?orderid=939056585568716&num=10&pt=1&sep=1"; //API链接
//    private static int num = 8500;
//    private static Integer id;
//    private static AgentUrl hostPort2 = null;
//    private static FilmUrl filmUrl = new FilmUrl();
//    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//    private static String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
//    static final org.apache.log4j.Logger logger = Logger.getLogger(FilmMessage.class);
//
//
//
//    public static void main(String[] args) throws IOException {
//        AgentUrl hostPort = getHostPort();
//        hostPort2 = hostPort;
//        while (true) {
//            List<String> startup = startup(hostPort2.getIp(), hostPort2.getPort());
//            for (int i = 0; i < startup.size(); i++) {
//                filmUrl.setFilm_url(startup.get(i));
//                filmUrl.setCreate_time(date);
//                SqlSessionFactory sqlSessionFactory = mybatisUtil.getSessionFactory();
//                //获取sql会话
//                SqlSession sqlSession = mybatisUtil.getSqlSession();
//
//                FilmUrlMapper filmUrlMapper =  sqlSession.getMapper(FilmUrlMapper.class);
//                filmUrlMapper.insertFilmUrl(filmUrl);
//                id =filmUrl.getId();
//                if (id != null){
//                    sqlSession.commit();
//                    sqlSession.close();
//
//                    FilmMessage filmMessage = new FilmMessage();
//                    logger.warn("爬取到的num："+num);
//                  //  FilmImage filmImage = new FilmImage();
//                    System.out.println("-------------------------");
//                    System.out.println("正在爬取路径"+startup.get(i));
//                 //   filmImage.startup(startup.get(i),id);
//                    filmMessage.startup(startup.get(i),id);
//                    System.out.println("-------------------------");
//                }
//            }
//        }
//       /* AgentUrl hostPort = getHostPort(apiUrl);
//        System.out.println(hostPort.getIp());
//        System.out.println(hostPort.getPort());*/
//    }
//
//
//    /**
//     * 启动项
//     * @return
//     */
//    public static List<String> startup(String ip, int port) throws IOException {
//        String url = spliturl();
//        System.out.println(url);
//        Document html = setAgent(url);
//        if (html== null){
//            html = setAgent(url);
//        }
//        List<String> list = GetUrlList(html.text());
//        if (list != null && list.size() > 0) {
//
//            return list;
//        }else {
//            startup(ip, port);
//        }
//        return null;
//    }
//
//    /**
//     * 获取接口的url(Json数组)
//     * @param url
//     * @return
//     * @throws IOException
//     */
//    public static Document setAgent(String url) {
//        if (url == null){
//            return null;
//        }
//        Document document = null;
//        try {
//            if (hostPort2 == null){
//                hostPort2 = getHostPort();
//            }
//            document = Jsoup.connect(url).proxy(hostPort2.getIp(), hostPort2.getPort())
//                    .ignoreContentType(true)
//                    .userAgent(randomUserAgent())
//                    .get();
//            return document;
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            hostPort2 = getHostPort();
//            setAgent(url);
//            return document;
//        }
//    }
//
//    public static int getRamdom(){
//        int random=new Random().nextInt(4);
//        return random+1;
//    }
//
//    /**
//     * 常用 user agent 列表
//     */
//    static List<String> USER_AGENT = new ArrayList<String>(10) {
//        {
//            add("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
//            add("Mozilla/5.0 (Linux; U; Android 4.0.4; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
//            add("Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
//            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
//            add("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0");
//            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
//            add("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
//            add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
//            add("Mozilla/5.0 (iPod; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3A101a Safari/419.3");
//        }
//    };
//    /**
//     * 随机获取 user agent
//     *
//     * @return
//     */
//    public static String randomUserAgent() {
//        Random random = new Random();
//        int num = random.nextInt(USER_AGENT.size());
//        return USER_AGENT.get(num);
//    }
//
//    /**
//     * 将url放进数组
//     * @param html
//     * @return
//     */
//    public static List<String> GetUrlList(String html) {
//        if (html == null /*&& html.contains("异常")*/) {
//
//        }
//        List<String> list = new ArrayList<>();
//        JSONObject object = (JSONObject) JSONObject.parse(html);
//           JSONArray result = object.getJSONArray("data");
//      //  JSONArray result = object.getJSONArray("subjects");
//        System.out.println(result);
//        if (result != null) {
//            for (int i = 0; i < result.size(); i++) {
//                String currentCity = result.getJSONObject(i).getString("url");
//                logger.info("开始爬取："+currentCity);
//                list.add(currentCity);
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 将接口url拼接一次获取（20个）
//     * @return
//     */
//    public static String spliturl() {
//        System.out.println("正在爬取第："+num+"开始的链接");
//        String url2  ="https://movie.douban.com/j/search_subjects?type=tv&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=20&page_start="+num;
//        String url = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=%E7%94%B5%E5%BD%B1&start=" + num;
//      //  String url3 ="https://movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=%E7%94%B5%E5%BD%B1&start="+num+"&genres=%E5%A5%87%E5%B9%BB";
//        String url4 ="https://movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=%E7%94%B5%E8%A7%86%E5%89%A7&start=" +num;
//        String url5 = "https://movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=&start=" +num;
//
//        num += 20;
//        return url5;
//    }
//
//
//}