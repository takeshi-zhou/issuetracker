package cn.edu.fudan.measureservice;


import com.alibaba.fastjson.JSONObject;
import org.powermock.core.ListMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException {
//          int a=2;
//          int b=3;
//        System.out.println((double)a/b);
//


        String t1="2013-10-31 ";
        String t2="2013-10-31 04:57:23";
        int result = t1.compareTo(t2);

        System.out.println(result);


        String until = "2018-02-22";
        String endDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date date = simpleDateFormat.parse(until);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endDate = simpleDateFormat.format(calendar.getTime());
        }catch(Exception e){

        }
        System.out.println(endDate);
    }
}
