package cn.edu.fudan.cloneservice.util;

public class AddrTransUtil {
    public static String AddrTrans(String in){
        // genson/src/test/java/com/owlike/genson/bean/Item.java
        String res = in.replace("/", "\\\\");
        res = "D:\\\\repo\\\\github\\\\genson\\\\" + res;
        return res;
    }
}
