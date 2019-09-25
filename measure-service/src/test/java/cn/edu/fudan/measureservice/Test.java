package cn.edu.fudan.measureservice;


import cn.edu.fudan.measureservice.domain.Developer;
import com.alibaba.fastjson.JSONObject;
import org.powermock.core.ListMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws IOException {
//          int a=2;
//          int b=3;
//        System.out.println((double)a/b);
//

        Developer developer =new Developer();
        String out = "Author: linwangzai <957859199@qq.com>";
        out = out.trim();
        String name="";
        String email="";
        String[] aaab = out.split("[\\s]+");
        for(int i= 1;i< aaab.length;i++){
            if(aaab[i].startsWith("<") && aaab[i].endsWith(">")){
                String judge;
                judge = aaab[i].substring(1,aaab[i].length()-1);
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
                Matcher matcher = pattern.matcher(judge);
                if (!matcher.matches()) {
                    throw new RuntimeException("invalid url!");
                }
                email = judge;
            }else{
                if("".equals(name)){
                    name = aaab[i];
                }else{
                    name = name + " " +aaab[i];
                }
            }
        }





        String[] sssss = new String[3];
        sssss = out.split("[\\s]+");
        developer.setEmail(sssss[2].substring(1,sssss[2].length()-1));
        System.out.println(sssss.toString());
    }
}
