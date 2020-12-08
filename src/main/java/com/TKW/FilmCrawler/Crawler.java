package com.TKW.FilmCrawler;

import com.TKW.FilmCrawler.Agent.AgentUrl;
import com.TKW.Utils.GsonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.TKW.FilmCrawler.Agent.Agent.getHostPort;


/**
 * 获取电影的信息
 */
public class Crawler {
    private static Logger logger = LoggerFactory.getLogger(Crawler.class);
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
    private static String s = HtmlUtils.htmlUnescape("&copy;");
    private static Film film = new Film();
    private static AgentUrl hostPort2 = null;


    /**
     * 给 豆瓣id 进行数据爬取
     * @param douBanId
     * @return
     */
    public static Film start(String douBanId){
        int doubanInt = Integer.parseInt(douBanId);
        String url ="https://movie.douban.com/subject/";
        Film startup = startup(url + douBanId, doubanInt);

     return startup;
    }


    public static Document setAgent(String url){
        Document document = null;

        if (hostPort2 == null){
            hostPort2 = getHostPort();
        }
        for (int i = 0; i < 3; i++) {
            try {
                document = Jsoup.connect(url).proxy(hostPort2.getIp(), hostPort2.getPort())
                        .ignoreContentType(true)
                        .userAgent(randomUserAgent())
                        .get();
            }catch (Exception e){
                continue;
            }
            if (document!=null){

                break;
            }
        }
        return document;
    }

    /**
     * 常用 user agent 列表
     */
    static List<String> USER_AGENT = new ArrayList<String>(10) {
        {
            add("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
            add("Mozilla/5.0 (Linux; U; Android 4.0.4; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            add("Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            add("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
            add("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
            add("Mozilla/5.0 (iPod; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3A101a Safari/419.3");
        }
    };
    /**
     * 随机获取 user agent
     *
     * @return
     */
    public static String randomUserAgent() {
        Random random = new Random();
        int num = random.nextInt(USER_AGENT.size());
        return USER_AGENT.get(num);
    }


    /**
     * 先用自己的 ip地址获取 页面 ，如果 被强开启代理
     * @param url
     * @return
     */
    public static Document agent(String url){
        Document document = null;
        try {
             document = Jsoup.connect(url).userAgent(randomUserAgent())
                    .timeout(5000)
                    .get();
        } catch (IOException e) {
            System.out.println("本身ip处理+"+e.getMessage());
            document = setAgent(url);
        }

       return document;

    }

    /**
     * 启动类
     * @return
     */
    public static Film startup(String url, Integer id) {
        film.setFilm_url_id(id);
        film.setFilmUrl(url);

       Document document = agent(url);

            if (document == null) {
                return null;
            }else {
                try {
                    getDouBanRating(document.toString());
                    getChineseName(document.toString());
                    getEnglishName(document.toString());
                    getFilmImage(document.toString());
                    String html = document.toString();
                    String fullInformation = getFullInformation(html);
                    if (!fullInformation.contains("集数")) {
                        film.setTag(getTages(html));
                        film.setDescription(getIntroduction(html));
                    } else {
                        film.setCategory(1);
                        film.setTag(getTages(html));
                        film.setDescription(getIntroduction(html));

                        operaStartUp(url, id);//电视剧的启动类
                    }
                    logger.info(String.valueOf(film));
                    String datePublished = film.getDatePublished();
                    if (datePublished == null) {
                        try {
                            film.setFilmYear(film.getPremiere().substring(0, 4));
                        } catch (Exception e) {

                        }
                    }
                }catch (Exception e){
                    startup(url, id);
                }

            }
       return film;
    }


    /**
     * 获取豆瓣id
     *
     * @param url
     * @return
     */
    public static String getDoubanId(String url) {
        if (url == null) {
            return null;
        }
        String pattern = "[0-9]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        int matcher_start = 0;
        while (m.find(matcher_start)) {
            matcher_start = m.end();
            film.setDoubanId(Integer.parseInt(m.group(0)));
            return (m.group(0));
        }
        return null;
    }

    public static String getFilmImage(String html) {
        Document parse = Jsoup.parse(html);
        Element mainpic = parse.getElementById("mainpic");
        //  System.out.println(mainpic);
        Elements img = mainpic.getElementsByTag("a");
        Elements img1 = img.get(0).getElementsByTag("img");
        String src = img1.attr("src");
        film.setFilmCoverImage(src);
        return null;
    }


    /**
     * 获取豆瓣最高分和总评价人数
     *
     * @param html
     * @return
     */
    public static String getDouBanRating(String html) {
        Document parse = Jsoup.parse(html);
        try {
            Elements rating_self_clearfix = parse.getElementsByClass("rating_self clearfix");
            String text = rating_self_clearfix.get(0).text();
            String[] s = text.split(" ");
            text.split("\\s+");
            if (!(s[0]).contains("暂无评分") || !s[0].contains("尚未上映")) {
                film.setRatingValue(Double.parseDouble(s[0]));
                film.setRatingCount(s[1]);
            } else {
                film.setRatingCount(("暂无评分"));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    /**
     * 中文名字
     */
    public static String getChineseName(String html) {
        String chinsesename = null;
        try {
            String pattern = "\\<title[^\\>]*\\>\\s*(?<Title>.*?)\\s*\\</title\\>";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(html);


            while (m.find()) {
                //    System.out.println(m.group());
                chinsesename =m.group(1);
            }

            String[] split = chinsesename.split("\\(");
            chinsesename =split[0];
            film.setChineseName(chinsesename);
        }catch (Exception e){

        }

        return chinsesename;
    }

    /**
     * 英文名字
     */
    public static String getEnglishName(String html) {
        String back = "";
        try {
            Document parse = Jsoup.parse(html);
            Elements elementsByTag = parse.getElementById("content").getElementsByTag("h1");
            String text = elementsByTag.get(0).text();
            String EnglishName = null;
            String pattern = "[a-zA-Z]+";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(text);
            while (m.find()) {
                EnglishName = m.group();
                back += EnglishName + " ";
            }
            film.setEnglishName(back);
        }catch (Exception e){

        }
        return back;
    }


    /**
     * 获取电影简介
     *
     * @param html
     * @return
     */
    public static String getIntroduction(String html) {
        Element elementById = Jsoup.parse(html).getElementById("link-report");
        String relIntroduction = null;
        try {
            relIntroduction = elementById.text().trim().replace(s + "豆瓣", "").substring(2);
            return relIntroduction;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 电视剧的每集简介启动类
     *
     * @param url
     * @return
     */
    public static List<String> operaStartUp(String url, int id) {
        System.out.println(url);
        FilmDrama filmDrama = new FilmDrama();
        String doubanId = getDoubanId(url);
        filmDrama.setDoubanId(doubanId);

        List<String> list = new ArrayList<>();
        Document document = setAgent(url);
        if (document == null) {
            document = setAgent(url);
        }
        String html = document.toString();
        if (html == null) {
            document = setAgent(url);
            html = document.toString();
        }
        List<String> seriesList = getSeriesList(html);
        for (int i = 0; i < seriesList.size(); i++) {
            filmDrama.setNum(i + 1);
            filmDrama.setDescription(getIntroductionOfSeries(seriesList.get(i)));
            filmDrama.setDoubanId(getDoubanId(url));
            filmDrama.setFilmUrlId(id);
            System.out.println(filmDrama);
        }
        return list;
    }


    /**
     * 获取电视剧的所有链接保存到list中
     *
     * @return
     */
    public static List<String> getSeriesList(String html) {
        List<String> listUrl = new ArrayList<>();
        Document parse = Jsoup.parse(html);
        String episode_list = parse.getElementsByClass("episode_list").html();
        Elements a = Jsoup.parse(episode_list).select("a");
        for (int i = 0; i < a.size(); i++) {
            listUrl.add(a.get(i).attr("href"));
        }
        return listUrl;
    }


    /**
     * 获取电视剧单集的简介
     *
     * @param url
     * @return
     */
    public static String getIntroductionOfSeries(String url) {
        String relurl = "https://movie.douban.com/" + url;
        String html = null;
        try {
            Document document = setAgent(relurl);
            Element elementById = document.getElementById("link-report");
            html = elementById.text().replace("更多»", "");
            return html;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return html;
    }

    /**
     * 获取标签所有的内容
     *
     * @param html
     * @return 悬疑@犯罪@人性@推理@中国大陆@剧情@网剧@小说改编
     */
    public static String getTages(String html) {
        String tages = new String();
        Document parse = Jsoup.parse(html);
        Elements elementsByClass = parse.getElementsByClass("tags-body");
        if (elementsByClass.size() == 0) {
            return null;
        }
        Element element = elementsByClass.get(0);
        String tag = element.text();

        tages = tag.replace(" ", "@");
        return tages;
    }

    /**
     * 获取电影最上方的所有信息
     *
     * @param html
     * @return
     */
    public static String getFullInformation(String html) {
        List<String> list = new ArrayList<>();
        Document parse = Jsoup.parse(html);
        Element info = parse.getElementById("info");
        // System.out.println(info);
        String message = info.text();
        Elements pl = info.getElementsByClass("pl");
        for (int i = 0; i < pl.size(); i++) {
            if (i == pl.size() - 1) {
                int begin1 = message.indexOf(pl.get(i).text());
                list.add(message.substring(begin1, message.length()).replace(" ", "").replace("/", "@"));
                break;
            }
            int begin = message.indexOf(pl.get(i).text());
            int end = message.indexOf(pl.get(i + 1).text());
            list.add(message.substring(begin, end).replace(" ", "").replace("/", "@"));
        }
        //  System.out.println(list);
        //获取的右边的答案遍历
        for (String s : list) {
            int i = s.indexOf(":");
            if (s.contains("导演:")) {
                film.setDirector(s.substring(i + 1));
            } else if (s.contains("编剧")) {
                film.setAuthor(s.substring(i + 1));
            } else if (s.contains("主演")) {
                film.setActor(s.substring(i + 1));
            } else if (s.contains("制片国家")) {
                film.setProductionCountry(s.substring(i + 1));
            } else if (s.contains("语言")) {
                film.setMoreLanguage(s.substring(i + 1));
            } else if (s.contains("上映日期")) {
                String substring = s.substring(i + 1);
                film.setFilmYear(substring.substring(0, 4));
                film.setDatePublished(s.substring(i + 1));
            } else if (s.contains("片长")) {
                film.setSingleLength(s.substring(i + 1));
            } else if (s.contains("又名")) {
                film.setOtherName(s.substring(i + 1));
            } else if (s.contains("链接")) {
                film.setImdbId(s.substring(i + 1));
            } else if (s.contains("官方网站")) {
                film.setOfficialWebsite(s.substring(i + 1).replace("@", "/"));
            } else if (s.contains("集数")) {
                try {
                    film.setEpisodeNumber(Integer.parseInt(s.substring(i + 1)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (s.contains("单集片长")) {
                film.setSingleLength(s.substring(i + 1));
            } else if (s.contains("类型")) {
                film.setMold(s.substring(i + 1));
            } else if (s.contains("首播")) {
                film.setPremiere(s.substring(i + 1));
            }
        }
        return info.toString();
    }

//
//    public static void main(String[] args) throws IOException {
//        String num = "30465260";
//
//        Film start = start(num);
//
//        System.out.println(GsonUtils.toJson(start));
//    }

}


