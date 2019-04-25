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

    private List<File> fileList;

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



    private String getRepoUrl(String repoId, String commitId) {
//        JSONObject response = restInterfaceManager.checkOut(repoId, commitId);
//        return response != null && response.getJSONObject("data").getString("status").equals("Successful");

        JSONObject jsonObject=  restInterfaceManager.getURL(repoId, commitId);
        JSONObject data= jsonObject.getJSONObject("data");
        String status = data.get("status").toString();
        String content = data.get("content").toString();
        if(status.equals("Successful")){
            logger.info("get url successful:" + content);
            logger.info("get repo url complete -> start the package scan operation......");
            return content;
        }else {
            logger.info("get url failed " + status + content);
            return "error";
        }
    }

    private boolean freeRepoUrl(String repoId, String repoUrl){
        JSONObject nj = restInterfaceManager.freeRepoPath(repoId,repoUrl);
        JSONObject njdata = nj.getJSONObject("data");
        String s = njdata.get("status").toString();
        if(s.equals("Successful")){
            logger.info("free repo successful");
            return true;
        }{
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId ,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,"clone" ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }
    //#1 先写scan方法
    private void startScan(String repo_id, String commit_id) {
        logger.info("method startScan" + repo_id+"  : "+commit_id);

        String repoId = repo_id;
        String commitId = commit_id;
        logger.info("startScan-->start to get URL " + repoId + commitId);

        //访问文件服务,如果失败发送错误消息，直接返回
        String repo_url = "nourl";
        try{
            repo_url = getRepoUrl(repoId, commitId);
            if(repo_url.equals("error")){
                logger.info("startScan-->" + repo_id + "," + commit_id +"Get url failed cannot start scan");
                return;
            }
            //#2 考虑上锁 释放锁

            //#3 获取分析结果 存入数据库
            fileList = new ArrayList<>();
            getFileList(repo_url);
            Map<String, List> map_name_method_file_count = getNameMethodFileCountMap(fileList);
            List<CloneInfo> lci = cloneInfoDao.getCloneInfoByRepoIdAndCommitId(repoId, commit_id);
            List<Map> list_distr = getCloneDistriMap(lci);
            // store this into db
            packageNameDao.insertPackageInfo(repoId, commit_id, map_name_method_file_count, list_distr);
            logger.info("startScan-->insert package info should be OK!");

        }catch (Exception e){
            logger.info("startScan-->" + e.getMessage());
        }
        finally {
            fileList = null;
            //now free the repo
            if(freeRepoUrl(repoId, repo_url) == true){
                logger.info("startScan-->Free url ok");
            }else{
                logger.info("startScan-->Free url failed");
            }
        }


    }

    public void run(String repoId, List<String> commit_list){        //对外启动接口

        for(String commit_id:commit_list){
            String status =  packageScanStatusDao.selectPackageScanStatusByRepoIdAndCommitId(repoId, commit_id);
            if(status != null && status.equals("Scanned")){
                continue;
            }else {
                startScan(repoId, commit_id);
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

    private void addLines(Set<Integer> si, String bug_lines){
        try {
            String div[] = bug_lines.split(",");
            for(String str:div){
                si.add(Integer.parseInt(str));
            }
        }catch (Exception e){
            logger.info("addLines-->");
        }

    }
    public List<Map> getCloneDistriMap(List<CloneInfo> lci){
        Map<String, Integer> ins_map = new HashMap<>();
        Map<String, Map> line_map = new HashMap<>();
        for(CloneInfo ci:lci) {
            try {
                String file_path = ci.getFile_path();
                String package_name = ci.getPackageName();
                String method_name = ci.getMethod_name();
                String bug_lines = ci.getBug_lines();
                if(ins_map.containsKey(package_name)){
                    ins_map.put(package_name, ins_map.get(package_name) + 1);
                }else{
                    ins_map.put(package_name, 1);
                }
                if(line_map.containsKey(package_name)){
                    Map<String, Set> subMap =  line_map.get(package_name);
                    if(subMap.containsKey(file_path)){
                        Set<Integer> si =  subMap.get(file_path);
                        addLines(si, bug_lines);
                    }else {
                        Set<Integer> si = new HashSet<>();
                        addLines(si,bug_lines);
                        subMap.put(file_path, si);
                    }
                }else {
                    Map<String, Set> file_line_map = new HashMap<>();
                    Set<Integer> si = new HashSet<>();
                    addLines(si,bug_lines);
                    file_line_map.put(file_path, si);
                    line_map.put(package_name, file_line_map);
                }



            }catch (Exception e){
                logger.info("getCloneDistriMap-->" + e.getMessage());
                continue;
            }

        }
        List<Map> res = new ArrayList<>();
        res.add(ins_map);
        res.add(line_map);
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
                String name;
                try {
                    name = cunit.getPackageDeclaration().get().getNameAsString();
                }catch (NoSuchElementException e){
                    name = "null";
                }
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
            catch (FileNotFoundException e){
                logger.info("getNameMethodFileCountMap-->" + e.toString());
                continue;
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
