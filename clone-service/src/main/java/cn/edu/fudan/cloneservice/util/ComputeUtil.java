package cn.edu.fudan.cloneservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zyh
 * @date 2020/6/15
 */
public class ComputeUtil {

    /**
     * 计算实际的clone行数
     * @param map
     * @return
     */
    public static int getCloneLines(Map<String, List<String>> map){

        int cloneLines = 0;
        for(String filePath : map.keySet()){
            cloneLines += map.get(filePath).size();
        }

        return cloneLines;
    }

    /**
     * 统计实际的clone行数map
     * @param map
     * @param num
     * @param filePath
     * @return
     */
    public static Map<String, List<String>> putNewNum(Map<String, List<String>> map, String num, String filePath){

        if(map.containsKey(filePath)){
            if (!map.get(filePath).contains(num)){
                map.get(filePath).add(num);
            }
        }else {
            List<String> list = new ArrayList<>();
            list.add(num);
            map.put(filePath, list);
        }

        return map;
    }
}
