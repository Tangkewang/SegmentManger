package com.TKW.Ssh;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.TKW.Data.HttpBackMessage.BitTorrent;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程执行linux的shell script
 *
 * @author Ickes
 * @since V0.1
 */
@Data
public class RemoteExecuteCommand {
    private static Logger log = LoggerFactory.getLogger(RemoteExecuteCommand.class);
    //字符编码默认是utf-8
    private static String DEFAULTCHART = "UTF-8";
    private Connection conn;
    private String ip ="";
    private String userName ="";
    private String userPwd ="";

    public RemoteExecuteCommand(String ip, String userName, String userPwd) {
        this.ip = ip;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public RemoteExecuteCommand() {
    }

    /**
     * 远程登录linux的主机
     *
     * @return 登录成功返回true，否则返回false
     * @author Ickes
     * @since V0.1
     */
    public Boolean login() {
        boolean flg = false;
        try {
            conn = new Connection(ip);
            conn.connect();//连接
            flg = conn.authenticateWithPassword(userName, userPwd);//认证
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flg;
    }

    /**
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     * @author Ickes
     * 远程执行shll脚本或者命令
     * @since V0.1
     */
    public String execute(String cmd) {
        String result = "";
        try {
            if (login()) {
                Session session = conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), DEFAULTCHART);
                System.out.println(result);
                //如果为得到标准输出为空，说明脚本执行出错了
                if (StringUtils.isBlank(result)) {
                    result = processStdout(session.getStderr(), DEFAULTCHART);
                }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 本地执行cmd 命令
     * @param cmd 返回 命令行打印的数据
     * @return
     */
    public void localExecute(String cmd) throws IOException {
      new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                ProcessBuilder pb = new ProcessBuilder("bash","-c",cmd);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                readProcessOutput(process.getInputStream(), System.out);
            }
        }).start();

    }

    private static void readProcessOutput(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("-end");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param cmd 即将执行的命令
     * @return 命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     * @author Ickes
     * 远程执行shll脚本或者命令
     * @since V0.1
     */
    public String executeSuccess(String cmd) {
        System.out.println("执行的是 success命令"+cmd);
        String result = "";
        try {
            if (login()) {
                Session session = conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), DEFAULTCHART);
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void segment(String cmd){
      Thread A =  new Thread(()->{
          execute(cmd);
        });
      A.start();
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in      输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     * @author Ickes
     * @since V0.1
     */
    private String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 开始一个  bt下载任务
     *
     * @param btURl 种子的 下载地址
     */
    public boolean startBtCmd(String btURl) {
        String cmd = "transmission-remote -a " + "\"" + btURl + "\"";
        String execute = execute(cmd);

        if (execute.contains("success")) {
            log.info("添加任务：" + btURl + "成功");
            return true;

        } else {
            log.error("添加任务：" + btURl + "失败");
            return false;
        }
    }


    /**
     * 更新  种子的 下载状态
     * @return Map <种子名字，状态类>
     */
    public Map<String, BitTorrent> btDownState() {
        String cmd = "transmission-remote -l";

        String execute = execute(cmd);

        String[] split = execute.split("\n");


        Map<String,BitTorrent> bitTorrentMap = new ConcurrentHashMap<>();
//        List<BitTorrent> bitTorrentList = new ArrayList<>();
        for (int i = 1; i < split.length - 1; i++) {
            String[] s = split[i].split(" ");

            List<String> list = new ArrayList<String>();

            for (int j = 0; j < s.length; j++) {

                if (s[j].equals("")) {

                } else {
                    list.add(s[j]);
                }
            }
            BitTorrent bitTorrent = new BitTorrent();
            bitTorrent.setId(Integer.parseInt(list.get(0)));
            bitTorrent.setDone(list.get(1));

            for (int k = 0; k < list.size(); k++) {
                String name ="";
                int num = 0;
                if (list.get(k).contains("Down") || list.get(k).contains("Idle") || list.get(k).contains("Seeding")) {
                    num= k+1;
                    for (int l = num; l < list.size(); l++) {
                        name += list.get(l)+" ";
                    }
                    bitTorrent.setName(name.substring(0,name.length()-1));
                    break;
                }
            }
            bitTorrentMap.put(bitTorrent.getName(),bitTorrent);
//            bitTorrentList.add(bitTorrent);
        }
        return bitTorrentMap;
    }

}