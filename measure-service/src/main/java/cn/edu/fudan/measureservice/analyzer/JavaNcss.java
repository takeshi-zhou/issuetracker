package cn.edu.fudan.measureservice.analyzer;

import cn.edu.fudan.measureservice.util.DirExplorer;
import cn.edu.fudan.measureservice.util.FileFilter;
import javancss.FunctionMetric;
import javancss.Javancss;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: ncss 调用并得到目标文件
 *
 * @author fancying
 * create: 2020-06-10 20:26
 **/
public class JavaNcss {

    /**
     * 得到某个地址下面所有文件的圈复杂度
     *
     * @param repoPath  目录地址
     * @return key 文件的相对地址 value 文件的圈复杂度
     */
    public static Map<String, Integer> getFileCcn(String repoPath) {
        File projectDir = new File(repoPath);
        List<String> pathList = new ArrayList<>();
        Map<String, Integer> result = new LinkedHashMap<>(128);
        Map<String, String> relativePath = new LinkedHashMap<>(128);
        new DirExplorer((level, path, file) -> !FileFilter.javaFilenameFilter(path),
                (level, path, file) -> {
                    pathList.add(file.getAbsolutePath());
                    relativePath.put(file.getAbsolutePath(), path);
                } ).explore(projectDir);

        for (String path : pathList) {
            int ccn = getFileCcn1(path);
            result.put(relativePath.get(path), ccn);
        }
        return result;
    }

    /**
     * 得到某个文件的圈复杂度
     *
     * @param path  文件地址
     * @return 文件的圈复杂度
     */
    public static int getFileCcn1(String path) {

        File tempFile = new File(path);
        Javancss javancss = new Javancss(tempFile);
        int ccn = 0;
        for (FunctionMetric functionMetric : javancss.getFunctionMetrics()) {
            ccn += functionMetric.ccn;
        }

        return ccn;
    }

}