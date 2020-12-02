package com.TKW.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 380276984@qq.com
 * @version 1.0.0
 * @date 2020/5/2 16:29
 */
public final class GsonUtils {
    private static Logger log = LoggerFactory.getLogger(GsonUtils.class);


    public  static String toJson(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException
    {
        try {
            Gson gson = new Gson();

            return  gson.fromJson(json, classOfT);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return  null;
        }

    }
}
