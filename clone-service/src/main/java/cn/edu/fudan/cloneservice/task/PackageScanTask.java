package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.CloneInfoDao;
import cn.edu.fudan.cloneservice.dao.PackageNameDao;
import cn.edu.fudan.cloneservice.dao.PackageScanStatusDao;

import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.domain.ScanResult;
import cn.edu.fudan.cloneservice.util.AddrTransUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.*;

@Component("packageScanTask")
public class PackageScanTask {
    private static final Logger logger= LoggerFactory.getLogger(PackageScanTask.class);

    private RestInterfaceManager restInterfaceManager;
    private KafkaTemplate kafkaTemplate;

    private PackageScanStatusDao packageScanStatusDao;
    private PackageNameDao packageNameDao;
    private CloneInfoDao cloneInfoDao;

    List<File> fileList = new ArrayList<>();

    @Autowired
    public void setCloneInfoDao(CloneInfoDao cloneInfoDao) {
        this.cloneInfoDao = cloneInfoDao;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }
    @Autowired
    public void setPackageScanStatusDao(PackageScanStatusDao packageScanStatusDao) {
        this.packageScanStatusDao = packageScanStatusDao;
    }
    @Autowired
    public void setPackageNameDao(PackageNameDao packageNameDao) {
        this.packageNameDao = packageNameDao;
    }



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
//        if (!checkOut(repoId, commitId)) {
//            send(repoId, commitId, "failed", "Package Scan - check out failed");
//            logger.error("Package Scan Check Out Failed!");
//            return;
//        }
        //TODO checkout
        logger.info("checkout complete -> start the package scan operation......");

        //#2 考虑上锁 释放锁

        //#3 获取分析结果 存入数据库
        List<File> fileList = getFileList(repoPath);
        Map<String, List> map_name_method_file_count = getNameMethodFileCountMap(fileList);
        List<CloneInfo> lci = cloneInfoDao.getCloneInfoByRepoIdAndCommitId(repoId, commit_id);
        Map<String, Integer> map_clone_dis = getCloneDistriMap(lci);
        // store this into db
        packageNameDao.insertPackageInfo(repoId, commit_id, map_name_method_file_count, map_clone_dis);
        System.out.println("Successed!");



    }

    public void run(String repoId, String repoName, String repoPath, List<String> commit_list){
        //对外启动接口

        for(String commit_id:commit_list){
            String status =  packageScanStatusDao.selectPackageScanStatusByRepoIdAndCommitId(repoId, commit_id);
            if(status != null && status.equals("Scanned")){
                continue;
            }else {
                startScan(repoId, repoName, repoPath, commit_id);
                packageScanStatusDao.insertPackageScanStatusByRepoIdAndCommitId(repoId, commit_id);
            }

        }

    }

    private  List<File> getFileList(String strPath) {

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

    public Map<String, Integer> getCloneDistriMap(List<CloneInfo> lci){
        Map<String, Integer> res = new HashMap<>();
        for(CloneInfo ci:lci) {
            String file_path = ci.getFile_path();
            //TODO need to be del when publish
            String file_path_transed = AddrTransUtil.AddrTrans(file_path);
            File f = new File(file_path_transed);

            try {
                CompilationUnit cunit = JavaParser.parse(f);
                String key = cunit.getPackageDeclaration().get().getNameAsString();
                if(res.containsKey(key)){
                    res.put(key, res.get(key) + 1);
                }else{
                    res.put(key, 1);
                }

            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

        }
        return res;
    }

    static class ClassLevelVisitor extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(PackageDeclaration node,Void arg){

//             System.out.println(node.getNameAsString());
        }

    }


    public Map<String, List> getNameMethodFileCountMap(List<File> filelist){
        Map<String, List> map_name_method_num = new HashMap<>();

        for(File file:filelist){
            try {
                //把一个java代码文本解析成单元
                CompilationUnit cunit = JavaParser.parse(file);
//                cunit.accept(new ClassLevelVisitor(),null);
                String name = cunit.getPackageDeclaration().get().getNameAsString();
                List<MethodDeclaration> lmd = getMethodList(cunit);
                List<Integer> list;
                if(!map_name_method_num.containsKey(name)){//if not contains this name
                    list = new ArrayList<>();
                    list.add(0);//index = 0 means method num
                    list.add(0);//index = 1 means file num
                    map_name_method_num.put(name, list);
                }
                list = map_name_method_num.get(name);
                list.set(0, list.get(0) +  lmd.size()) ;//update method num
                list.set(1, list.get(1) + 1);//update file num
            }
            catch (NoSuchElementException e){
//					e.printStackTrace();
                continue;
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }
        return map_name_method_num;
    }

    public static List<MethodDeclaration> getMethodList(CompilationUnit cu) {
        List<MethodDeclaration> methodList = new ArrayList<MethodDeclaration>();
        new MethodGetterVisitor().visit(cu, methodList);//访问单元内的节点
        return methodList;
    }

    private static class MethodGetterVisitor extends VoidVisitorAdapter<Object> {

        @SuppressWarnings("unchecked")
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            List<MethodDeclaration> methodList = new ArrayList<MethodDeclaration>();
            methodList =  (List<MethodDeclaration>) arg;
            methodList.add(n);
        }
    }


}
