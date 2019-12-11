package cn.edu.fudan.issueservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ProjectName: issueTracker
 * @Package: cn.edu.fudan.issueservice.util
 * @ClassName: RegexUtil
 * @Description:
 * @Author: bruce
 * @CreateDate: 2019/12/6 14:36
 * @Version: 1.0
 */
public class RegexUtil {
    public static String getNoTagCode(String code, Map<List<String>,String> regexAndReplaceStr){
        Set<List<String>> regexStrsSet = regexAndReplaceStr.keySet();
        for (List<String> regexStrs:regexStrsSet) {
            String replaceStr = regexAndReplaceStr.get(regexStrs);
            for (String regexStr:regexStrs) {
                Pattern pattern = Pattern.compile(regexStr);
                Matcher matcher = pattern.matcher(code);
                code = matcher.replaceAll(replaceStr);
            }
        }
        return code;
    }
}
