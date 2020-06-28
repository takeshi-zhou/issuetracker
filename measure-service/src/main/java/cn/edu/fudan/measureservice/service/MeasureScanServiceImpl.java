package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.JavaNcss;
import cn.edu.fudan.measureservice.annotation.RepoResource;
import cn.edu.fudan.measureservice.domain.Function;
import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.domain.core.FileMeasure;
import cn.edu.fudan.measureservice.domain.core.MeasureScan;
import cn.edu.fudan.measureservice.domain.dto.RepoResourceDTO;
import cn.edu.fudan.measureservice.mapper.FileMeasureMapper;
import cn.edu.fudan.measureservice.mapper.MeasureScanMapper;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.JGitHelper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-11 10:48
 **/
@Slf4j
@Service
public class MeasureScanServiceImpl implements MeasureScanService {

    private RepoMeasureMapper repoMeasureMapper;
    private PackageMeasureMapper packageMeasureMapper;
    private FileMeasureMapper fileMeasureMapper;
    private MeasureScanMapper measureScanMapper;

    public MeasureScanServiceImpl(RepoMeasureMapper repoMeasureMapper, PackageMeasureMapper packageMeasureMapper, FileMeasureMapper fileMeasureMapper, MeasureScanMapper measureScanMapper) {
        this.repoMeasureMapper = repoMeasureMapper;
        this.packageMeasureMapper = packageMeasureMapper;
        this.fileMeasureMapper = fileMeasureMapper;
        this.measureScanMapper = measureScanMapper;
    }

    private MeasureScan initMeasureScan(String repoId, String toolName, String startCommit,
                                 String endCommit, int totalCommitCount){
        String uuid = UUID.randomUUID().toString();
        int scannedCommitCount = 0;
        int scanTime = 0;
        String status = "scanning";
        Date startScanTime = new Date();
        Date endScanTime = new Date();
        MeasureScan measureScan = new MeasureScan();
        measureScan.setUuid(uuid);
        measureScan.setRepoId(repoId);
        measureScan.setTool(toolName);
        measureScan.setStartCommit(startCommit);
        measureScan.setEndCommit(endCommit);
        measureScan.setTotalCommitCount(totalCommitCount);
        measureScan.setScannedCommitCount(scannedCommitCount);
        measureScan.setScanTime(scanTime);
        measureScan.setStatus(status);
        measureScan.setStartScanTime(startScanTime);
        measureScan.setEndScanTime(endScanTime);
        try {
            measureScanMapper.insertOneMeasureScan(measureScan);
        } catch (Exception e) {
            log.error("向measure_scan表插入数据时报错：");
            e.printStackTrace();
        }

        return measureScan;

    }

    private void updateMeasureScan(MeasureScan measureScan, String endCommit, int scannedCommitCount,
                                          int scanTime, String status, Date endScanTime){
        measureScan.setEndCommit(endCommit);
        measureScan.setScannedCommitCount(scannedCommitCount);
        measureScan.setScanTime(scanTime);
        measureScan.setStatus(status);
        measureScan.setEndScanTime(endScanTime);
        measureScanMapper.updateMeasureScan(measureScan);
    }

    /**
     * 保存某个项目某个commit的扫描信息
     */
    private void saveMeasureData(String repoId, String commitId, String commitTime, String repoPath) {
        try{
            Measure measure =  JavaNcss.analyse(repoPath);
            saveRepoLevelMeasureData(measure,repoId,commitId,commitTime,repoPath);
            //savePackageMeasureData(measure,repoId,commitId,commitTime);
            saveFileMeasureData(repoId,commitId,commitTime,repoPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取单个项目某个commit的Measure度量值
     */
    private Measure getMeasureDataOfOneCommit(String repoPath){
        Measure measure = null;
        try{
            if(repoPath != null) {
                measure = JavaNcss.analyse(repoPath);
            }
        }catch (Exception e){
            log.error("获取某commit的Measure时出错：");
            e.printStackTrace();
        }
        return measure;
    }

    /**
     * 保存某个项目某个commit项目级别的度量
     */
    private void saveRepoLevelMeasureData(Measure measure,String repoId,String commitId,String commitTime,String repoPath){
        try{
            RepoMeasure repoMeasure=new RepoMeasure();
            repoMeasure.setUuid(UUID.randomUUID().toString());
            repoMeasure.setFiles(measure.getTotal().getFiles());
            repoMeasure.setNcss(measure.getTotal().getNcss());
            repoMeasure.setClasses(measure.getTotal().getClasses());
            repoMeasure.setFunctions(measure.getTotal().getFunctions());
            repoMeasure.setCcn(measure.getFunctions().getFunctionAverage().getNcss());
            repoMeasure.setJava_docs(measure.getTotal().getJavaDocs());
            repoMeasure.setJava_doc_lines(measure.getTotal().getJavaDocsLines());
            repoMeasure.setSingle_comment_lines(measure.getTotal().getSingleCommentLines());
            repoMeasure.setMulti_comment_lines(measure.getTotal().getMultiCommentLines());
            repoMeasure.setCommit_id(commitId);
            repoMeasure.setCommit_time(commitTime);
            repoMeasure.setRepo_id(repoId);

            //调用JGit，先实例化JGit对象
            JGitHelper jGitHelper = new JGitHelper(repoPath);
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repoPath,commitId);

            String developerName = revCommit.getAuthorIdent().getName();
            String developerEmail = revCommit.getAuthorIdent().getEmailAddress();
            String commitMessage = revCommit.getShortMessage();
            if (revCommit.getParentCount()!=0){
                String firstParentCommitId = revCommit.getParent(0).getId().getName();
                repoMeasure.setFirst_parent_commit_id(firstParentCommitId);
            }
            if (revCommit.getParentCount()==2){
                String secondParentCommitId = revCommit.getParent(1).getId().getName();
                repoMeasure.setSecond_parent_commit_id(secondParentCommitId);
            }
            repoMeasure.setDeveloper_name(developerName);
            repoMeasure.setDeveloper_email(developerEmail);
            repoMeasure.setCommit_message(commitMessage);



            //获取该commit是否是merge
            repoMeasure.set_merge(jGitHelper.isMerge(revCommit));

            //如果是最初始的那个commit，那么工作量记为0，否则  则进行git diff 对比获取工作量
            boolean isInitCommitByJGit = jGitHelper.isInitCommit(revCommit);
            if (isInitCommitByJGit){
                repoMeasure.setAdd_lines(0);
                repoMeasure.setDel_lines(0);
                repoMeasure.setAdd_comment_lines(0);
                repoMeasure.setDel_comment_lines(0);
                repoMeasure.setChanged_files(0);
            }else{
                Map<String, Integer> map = getLinesDataByJGit(jGitHelper, repoPath, commitId);
                repoMeasure.setAdd_lines(map.get("addLines"));
                repoMeasure.setDel_lines(map.get("delLines"));
                repoMeasure.setAdd_comment_lines(map.get("addCommentLines"));
                repoMeasure.setDel_comment_lines(map.get("delCommentLines"));
                repoMeasure.setChanged_files(getChangedFilesCount(jGitHelper, repoPath, commitId));
            }



            try{
                if(repoMeasureMapper.sameMeasureOfOneCommit(repoId,commitId)==0) {
                    repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
                    log.info("Successfully insert one record to repo_measure table ：repoId is " + repoId + " commitId is " + commitId);
                }
            } catch (Exception e) {
                log.error("Inserting data to DB repo_measure table failed：");
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("Saving commit measure data failed：repoId is " + repoId + " commitId is " + commitId);
            e.printStackTrace();
        }

    }

//    /**保存某个项目某个commit包级别的度量
//     *
//     * @param measure
//     * @param repoId
//     * @param commitId
//     * @param commitTime
//     */
//    private void savePackageMeasureData(Measure measure,String repoId,String commitId,String commitTime){
//        try{
//            List<Package> packages =new ArrayList<>();
//            DecimalFormat df=new DecimalFormat("#.00");
//            for(Package p:measure.getPackages().getPackages()){
//                p.setUuid(UUID.randomUUID().toString());
//                p.setCommit_id(commitId);
//                p.setCommit_time(commitTime);
//                p.setRepo_id(repoId);
//                if(packageMeasureMapper.samePackageMeasureExist(repoId,commitId,p.getName())>0) {
//                    continue;
//                }
//                String packageName=p.getName();
//                int count=0;
//                int ccn=0;
//                for(Function function:measure.getFunctions().getFunctions()){
//                    if(function.getName().startsWith(packageName)){
//                        count++;
//                        ccn+=function.getCcn();
//                    }
//                }
//                if(count==0) {
//                    p.setCcn(0.00);
//                } else{
//                    double result=(double)ccn/count;
//                    p.setCcn(Double.valueOf(df.format(result)));
//                }
//                packages.add(p);
//            }
//            if(!packages.isEmpty()){
//                packageMeasureMapper.insertPackageMeasureDataList(packages);
//            }
//        } catch (NumberFormatException e) {
//            log.error("Saving package measure data failed：");
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 保存某个项目某个commit文件级别的度量
     * fixme 未考虑rename的情况 目前先在jgitHelper {@link JGitHelper# getDiffEntry} 中修改了rename处理
     */
    private void saveFileMeasureData(String repoId, String commitId, String commitTime, String repoPath) {
        FileMeasure fileMeasure = new FileMeasure();

        fileMeasure.setRepoId(repoId);
        fileMeasure.setCommitId(commitId);
        fileMeasure.setCommitTime(commitTime);

        //调用JGit，先实例化JGit对象
        JGitHelper jGitHelper = new JGitHelper(repoPath);

        //获取本次commit所有文件的代码变更情况
        List<Map<String,Object>> fileLinesData = getFileLinesDataByJGit(jGitHelper,repoPath,commitId);


        //得到变更文件list
        List<String> filePathList = getChangedFilePathList(jGitHelper, repoPath, commitId);

        List<FileMeasure> fileMeasureList = new ArrayList<>(filePathList.size());
        for (String filePath : filePathList){
            fileMeasure.setUuid(UUID.randomUUID().toString());
            fileMeasure.setFilePath(filePath);
            log.info("fileFullPath is: "+repoPath+'/'+filePath);
            fileMeasure.setCcn(JavaNcss.getOneFileCcn(repoPath+'/'+filePath));
            fileMeasure.setTotalLine(JavaNcss.getFileTotalLines(repoPath+'/'+filePath));
            //根据filePath，获取对应文件的代码行变动情况
            for (int i = 0; i < fileLinesData.size(); i++){
                if (fileLinesData.get(i).get("filePath").equals(filePath)){
                    fileMeasure.setAddLine(Integer.parseInt(fileLinesData.get(i).get("addLines").toString()));
                    fileMeasure.setDeleteLine(Integer.parseInt(fileLinesData.get(i).get("delLines").toString()));
                    break;
                }
            }
            fileMeasureList.add(new FileMeasure(fileMeasure));
        }
        jGitHelper.checkout(jGitHelper.getSingleParent(commitId));
        for (FileMeasure f : fileMeasureList){
            String filePath = f.getFilePath();
            int preCcn = JavaNcss.getOneFileCcn(repoPath+'/'+filePath);
            fileMeasure.setDiffCcn(fileMeasure.getCcn() - preCcn);
        }

        try{
            fileMeasureList.forEach(
                    f -> {
                        if (fileMeasureMapper.sameMeasureOfOneFile(f.getRepoId(),f.getCommitId(),f.getFilePath()) == 0){
                            fileMeasureMapper.insertOneFileMeasure(f);
                        }
                        log.info("Successfully insert one record to file_measure table ：repoId is " + f.getRepoId() + " commitId is " + f.getCommitId());
                    }
            );
        } catch (Exception e) {
            log.error("Inserting data to DB file_measure table failed：");
            e.printStackTrace();
        }

    }


    /**
     * @param jGitHelper
     * @param repo_path
     * @param commit_id
     * @return 通过JGit获取一次commit中开发者的新增行数，删除行数，新增注释行数，删除注释行数
     */
    private Map<String, Integer> getLinesDataByJGit(JGitHelper jGitHelper, String repo_path, String commit_id){
        Map<String, Integer> map = new HashMap<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            if (jGitHelper.isMerge(revCommit)){
                List<DiffEntry> mergeDiffFix = jGitHelper.getConflictDiffEntryList(commit_id);//获取merge情况变更的文件列表
                map = JGitHelper.getLinesData(mergeDiffFix);
            } else {
                List<DiffEntry> diffFix = JGitHelper.getChangedFileList(revCommit,repository);//获取非merge情况变更的文件列表
                map = JGitHelper.getLinesData(diffFix);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @return 通过JGit获取一次commit中每个文件的新增行数，删除行数
     */
    private List<Map<String,Object>> getFileLinesDataByJGit(JGitHelper jGitHelper, String repo_path, String commit_id){
        List<Map<String,Object>> result = new ArrayList<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            if (jGitHelper.isMerge(revCommit)){
                List<DiffEntry> mergeDiffFix = jGitHelper.getConflictDiffEntryList(commit_id);//获取merge情况变更的文件列表
                result = JGitHelper.getFileLinesData(mergeDiffFix);
            } else {
                List<DiffEntry> diffFix = JGitHelper.getChangedFileList(revCommit,repository);//获取非merge情况变更的文件列表
                result = JGitHelper.getFileLinesData(diffFix);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param jGitHelper
     * @param repo_path
     * @param commit_id
     * @return 通过JGit获取本次commit修改的文件数量
     */
    private int getChangedFilesCount(JGitHelper jGitHelper, String repo_path, String commit_id){
        int result = 0;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            List<DiffEntry> diffFix = JGitHelper.getChangedFileList(revCommit,repository);//获取变更的文件列表
            result = JGitHelper.getChangedFilesCount(diffFix);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过jgit获取修改文件的路径名 list
     */
    private List<String> getChangedFilePathList(JGitHelper jGitHelper, String repo_path, String commit_id){
        List<String> result = new ArrayList<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            List<DiffEntry> diffEntries;
            if (jGitHelper.isMerge(revCommit)){
                //获取merge情况变更的文件列表
                diffEntries = jGitHelper.getConflictDiffEntryList(commit_id);
            } else {
                //获取非merge情况变更的文件列表
                diffEntries = JGitHelper.getChangedFileList(revCommit,repository);
            }
            result = jGitHelper.getChangedFilePathList(diffEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public Object getScanStatus(String repoId) {
        List<Map<String, Object>> result = measureScanMapper.getScanStatus(repoId);
        for (int i = 0; i < result.size(); i++){
            Map<String, Object> map;
            map = result.get(i);
            //将数据库中timeStamp/dateTime类型转换成指定格式的字符串 map.get("commit_time") 这个就是数据库中dateTime类型
            String startScanTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(map.get("startScanTime"));
            map.put("startScanTime",startScanTime);
            String endScanTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(map.get("endScanTime"));
            map.put("endScanTime",endScanTime);
        }
        return result.get(0);
    }



    @Override
    @RepoResource
    @Async("taskExecutor")
    public void scan(RepoResourceDTO repoResource, String branch, String beginCommit, String toolName) {
        String repoPath = repoResource.getRepoPath();
        String repoId = repoResource.getRepoId();

        //1. 判断beginCommit是否为空,为空则表示此次为update，不为空表示此次为第一次扫描
        // 若是update，则获取最近一次扫描的commit_id，作为本次扫描的起始点
        if (StringUtils.isEmpty(beginCommit)){
            beginCommit = repoMeasureMapper.getLastScannedCommitId(repoId);
        }

        if (StringUtils.isEmpty(repoPath)){
            log.error("repoId:[{}] path is empty", repoId);
            return;
        }

        JGitHelper jGitHelper = new JGitHelper(repoPath);
        // 获取从 beginCommit 开始的 commit list 列表
        List<String> commitList = jGitHelper.getCommitListByBranchAndBeginCommit(branch, beginCommit);
        //初始化本次扫描状态信息
        MeasureScan measureScan = initMeasureScan(repoId, toolName, commitList.get(0), commitList.get(0), commitList.size());
        Date startScanTime = measureScan.getStartScanTime();

        int i = 0;
        // 遍历列表 进行扫描
        for (String commit : commitList) {
            String commitTime = jGitHelper.getCommitTime(commit);
            log.info("Start to scan measure info: repoId is {} commit is {}", repoId, commit );
            saveMeasureData(repoId, commit, commitTime, repoPath);
            //更新本次扫描状态信息
            Date currentTime = new Date();
            int scanTime = (int) (currentTime.getTime()-startScanTime.getTime()) / 1000;
            String status = "scanning";
            //扫描到最后一个commit
            if (i == commitList.size()-1){
                status = "complete";
            }
            updateMeasureScan(measureScan, commit, i++, scanTime, status, currentTime);
        }
        log.info("Measure scan complete!");
    }
}