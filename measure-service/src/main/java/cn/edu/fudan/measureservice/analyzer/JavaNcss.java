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
            int ccn = getOneFileCcn(path);
            result.put(relativePath.get(path), ccn);
        }
        return result;
    }

    /**
     * 得到指定文件（list）的圈复杂度
     *
     * @param filePathList  文件的绝对地址
     * @param preFix 代码库地址前缀  getRepoPath 得到的目录地址
     * @return key 文件的相对地址 value 文件的圈复杂度
     */
    public static Map<String, Integer> getFileCcn(List<String> filePathList, String preFix) {
        Map<String, Integer> result = new LinkedHashMap<>(128);
        for (String path : filePathList) {
            // 构造相对地址
            String relativePath = path.replace(preFix, "");
            int ccn = getOneFileCcn(path);
            result.put(relativePath, ccn);
        }
        return result;
    }

    /**
     * 得到某个文件的圈复杂度
     *
     * @param path  文件地址
     * @return 文件的圈复杂度
     */
    public static int getOneFileCcn(String path) {

        File tempFile = new File(path);
        Javancss javancss = new Javancss(tempFile);
        int ccn = 0;
        for (FunctionMetric functionMetric : javancss.getFunctionMetrics()) {
            ccn += functionMetric.ccn;
        }
        return ccn;
    }

    public static int getFileTotalLines(String path){
        File tempFile = new File(path);
        Javancss javancss = new Javancss(tempFile);
        return javancss.getNcss();
    }

    public static void main(String[] args) {
        String path="D:\\Project\\FDSELab\\IssueTracker-Master\\measure-service\\src\\main\\java\\cn\\edu\\fudan\\measureservice\\analyzer\\JavaNcss.java";
        System.out.println(getOneFileCcn(path));
        System.out.println(getFileTotalLines(path));
    }

}