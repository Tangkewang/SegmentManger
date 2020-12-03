package com.TKW.Test;

import com.TKW.Data.HttpBackMessage.BitTorrent;
import com.TKW.Ssh.RemoteExecuteCommand;
import com.TKW.Utils.GsonUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SshTest {
    public static void main(String[] args) {

        List<String> cmd = new ArrayList<>();
        cmd.add("bash");
        cmd.add("-c");
        cmd.add("cd");
        cmd.add("/root");
        cmd.add("ls");

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            readProcessOutput(process.getInputStream(), System.out);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
