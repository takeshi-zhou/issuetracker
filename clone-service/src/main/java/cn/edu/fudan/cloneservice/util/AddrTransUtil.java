package cn.edu.fudan.cloneservice.util;

import javafx.scene.shape.Path;

import java.io.File;

public class AddrTransUtil {
    public static String AddrTrans(String in, String repo_url){
        // genson/src/test/java/com/owlike/genson/bean/Item.java
        String res = repo_url + File.separator + in;
        return res;
    }
}
