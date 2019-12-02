package cn.edu.fudan.scanservice.util;

import java.util.HashMap;
import java.util.Map;

public class SearchUtil {

    public static Map<String,String> dichotomy(String[] strings, String value){
        Map<String,String> result = new HashMap<>();
        int middle;
        int start =0;
        int end = strings.length-1;
        while (start <= end) {
            if(start == end ){
                int compareResult = value.compareTo(strings[end]);
                if(compareResult==0){
                    result.put("matching",String.valueOf(true));
                    result.put("location",String.valueOf(end));
                }else if (compareResult<0){
                    result.put("matching",String.valueOf(false));
                    result.put("location",String.valueOf(end));
                }else{
                    result.put("matching",String.valueOf(false));
                    result.put("location",String.valueOf(end+1));
                }
                return result;
            }else{
                middle = (start+end)/2;
                int compareResult = value.compareTo(strings[middle]);
                if(compareResult==0){
                    result.put("matching",String.valueOf(true));
                    result.put("location",String.valueOf(middle) );
                    return result;
                }else if(compareResult==-1){
                    end = --middle;
                }else{
                    start=++middle;
                }
            }
        }
        return result;
    }
}
