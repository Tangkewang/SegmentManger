package com.TKW.Test;

import com.TKW.Data.HttpBackMessage.BitTorrent;
import com.TKW.Ssh.RemoteExecuteCommand;
import com.TKW.Utils.GsonUtils;

import java.util.List;
import java.util.Map;

public class SshTest {
    public static void main(String[] args) {
        String ip = "62.210.249.200";
        String name = "root";
        String password = "Phliyundi888999";

        RemoteExecuteCommand executeCommand = new RemoteExecuteCommand(ip,name,password);
//        String btUrl ="https://pt.m-team.cc/download.php?id=444420&passkey=9f1e9849aa4d5eb1daa7f313c47c794b&ipv6=1&https=1";
//        executeCommand.startBtCmd(btUrl);

        Map<String, BitTorrent> bitTorrentMap = executeCommand.btDownState();

        System.out.println(GsonUtils.toJson(bitTorrentMap));
    }
}
