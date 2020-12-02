package com.TKW.BtDownLoad;

public class BtDown {

    public static void startBtCmd(String url){
        String cmd = "transmission-remote -a " +"\""+url + "\"";

    }
}
