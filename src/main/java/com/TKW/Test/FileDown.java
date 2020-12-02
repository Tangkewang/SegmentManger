package com.TKW.Test;

import java.io.File;

public class FileDown {
    public static void main(String[] args) {
        String path ="C:\\Users\\NING MEI\\Desktop\\fileTest\\The haha wkwk";

        File file = new File(path);

        if (file.exists()){
            System.out.println("success");
        }
    }
}
