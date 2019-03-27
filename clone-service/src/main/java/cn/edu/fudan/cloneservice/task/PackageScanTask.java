package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.domain.Scan;
import cn.edu.fudan.cloneservice.domain.ScanResult;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

@Component("PackageScanTask")
public class PackageScanTask {
    private static final Logger logger= LoggerFactory.getLogger(CloneScanTask.class);

    private RestInterfaceManager restInterfaceManager;
    private KafkaTemplate kafkaTemplate;

    private boolean checkOut(String repoId, String commitId) {
        JSONObject response = restInterfaceManager.checkOut(repoId, commitId);
        return response != null && response.getJSONObject("data").getString("status").equals("Successful");
    }
    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId ,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,"clone" ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }
    //#1 先写scan方法
    private void startScan(String repoId, String repoName, String repoPath, String commit_id) {
        logger.info(repoPath+"  : "+repoName);

        String commitId= commit_id;
        logger.info("start to checkout -> " + commitId);

        //checkout,如果失败发送错误消息，直接返回
        if (!checkOut(repoId, commitId)) {
            send(repoId, commitId, "failed", "Package Scan - check out failed");
            logger.error("Package Scan Check Out Failed!");
            return;
        }
        logger.info("checkout complete -> start the package scan operation......");

        List<File> fileList = getFileList(repoPath);


    }

    //#2 考虑上锁 释放锁

    //#3 获取分析结果 存入数据库
    private  List<File> getFileList(String strPath) {
        List<File> fileList = new ArrayList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileName.endsWith(".java")) { // 判断文件名是否以.avi结尾
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }

        }
        return fileList;
    }
    static class ClassLevelVisitor extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(PackageDeclaration node,Void arg){

//             System.out.println(node.getNameAsString());
        }

    }
    public Set<String> getName(List<File> filelist){
        Set<String> package_name_set = new HashSet<>();
        for(File file:filelist){
            try {
                CompilationUnit cunit = JavaParser.parse(file);
                cunit.accept(new ClassLevelVisitor(),null);
                String name = cunit.getPackageDeclaration().get().getNameAsString();
                package_name_set.add(name);
            }
            catch (NoSuchElementException e){
//					e.printStackTrace();
                continue;
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }
        return package_name_set;
    }


}
