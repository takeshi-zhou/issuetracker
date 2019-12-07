package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.*;
import cn.edu.fudan.cloneservice.domain.*;
import cn.edu.fudan.cloneservice.mapper.RepoCommitMapper;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.util.DateTimeUtil;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import cn.edu.fudan.cloneservice.util.LineNumUtil;
import org.eclipse.jgit.gitrepo.RepoCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */

@Service
public class CloneMeasureServiceImpl implements CloneMeasureService {
    @Autowired
    CloneInstanceInfoDao cloneInstanceInfoDao;

    @Autowired
    RepoCommitMapper repoCommitMapper;

//    @Autowired
//    IssueDao issueDao;
//
//    @Autowired
//    RawIssueDao rawIssueDao;
//
//    @Autowired
//    CommitDao commitDao;
//
//    @Autowired
//    LocationDao locationDao;

    @Autowired
    RepoMeasureDao repoMeasureDao;

    @Autowired
    private RestInterfaceManager restInterfaceManager;
    @Value("${repoHome}")
    private String repoHome;

    private long getCloneLine(List<CloneInstanceInfo> lci){
        long res = 0;
        Map<String, Set<Integer>> file_map = new HashMap<>();
        for (CloneInstanceInfo ci:lci){
            if(!file_map.containsKey( ci.getFile_path()) ){
                file_map.put(ci.getFile_path(), new HashSet<>());
            }
            for( Integer integer = ci.getStart_line(); integer <= ci.getEnd_line(); integer ++){
                file_map.get(ci.getFile_path()).add(integer);
            }
        }

        for(String key:file_map.keySet()){
            res += file_map.get(key).size();
        }
        return res;
    }

    @Override
    public RepoCloneMeasureData getRepoMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id) {
        List<CloneInstanceInfo> lci =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
//        Integer sum = 0;
//        for (CloneInstanceInfo cloneInstanceInfo: lci){
//            sum += (cloneInstanceInfo.getEnd_line() - cloneInstanceInfo.getStart_line() + 1);
//        }
        long sum =  getCloneLine(lci);
        long total_line = getRepoTotalLine(repo_id, commit_id);
        RepoCloneMeasureData repoCloneMeasureData = new RepoCloneMeasureData(repo_id, commit_id, sum, total_line);

        return repoCloneMeasureData;
    }

    @Override
    public RepoCloneRatio getRepoCloneRatioByRepoIdCommitId(String repo_id, String commit_id) {
        List<CloneInstanceInfo> lci =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
        long clone_line =  getCloneLine(lci);

        //get total line
        long total_line = getRepoTotalLine(repo_id, commit_id);
        Double ratio = 1.0 * clone_line / total_line;
        return new RepoCloneRatio(repo_id, commit_id, ratio);
    }

    public long getRepoTotalLine(String repo_id, String commit_id){
        String repoPath=null;
        try {
            repoPath = restInterfaceManager.getRepoPath(repo_id, commit_id);
            if (repoPath != null) {
                LineNumUtil lineNumUtil = new LineNumUtil();
                long repo_line = lineNumUtil.getRepoLineNumber(repoPath);
                return repo_line;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(repoPath!=null) {
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }
        }
        return -1;
    }

    @Override
    public List<RepoCloneInfoMonthly> getRepoCloneInfoByRepoId(String repo_id) {
        List<RepoCloneInfoMonthly> result=new ArrayList<>();
        //获取一个repo最新最近的一次commit的日期last_day
        String last_day_str = repoCommitMapper.getLastDateOfRepo(repo_id).substring(0,10);;
        LocalDate last_day=LocalDate.parse(last_day_str, DateTimeUtil.Y_M_D_formatter);
        LocalDate first_day=last_day.minusDays(30);
        String first_day_str = first_day.toString().substring(0,10);
        List<Commit> commit_list = repoCommitMapper.selectCommitByRepoIdAndDuration(repo_id,first_day_str,last_day_str);

        //统计最近一次的commit，单独统计是为了方便循环判断和上一次commit日期是否相同
        Commit commit0 = commit_list.get(0);
        String commit_id0 = commit0.getCommit_id();
        List<CloneInstanceInfo> lci0 =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id0);
        long clone_line0 =  getCloneLine(lci0);
        long total_line0 = getRepoTotalLine(repo_id, commit_id0);
        Double ratio0 = 1.0 * clone_line0 / total_line0;
        RepoCloneInfoMonthly repoCloneInfoMonthly0 = new RepoCloneInfoMonthly(last_day_str,repo_id, commit_id0, clone_line0,total_line0,ratio0);
        result.add(repoCloneInfoMonthly0);

        //循环从倒数第二次commit开始
        for (int i=1; i<commit_list.size(); i++){
            Commit commit = commit_list.get(i);
            if (!commit.getCommit_time().substring(0, 10).equals(last_day_str)){
                String commit_id = commit.getCommit_id();
                String commit_date = commit.getCommit_time().substring(0,10);
                List<CloneInstanceInfo> lci =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
                long clone_line =  getCloneLine(lci);
                long total_line = getRepoTotalLine(repo_id, commit_id);
                Double ratio = 1.0 * clone_line / total_line;
                RepoCloneInfoMonthly repoCloneInfoMonthly = new RepoCloneInfoMonthly(commit_date,repo_id, commit_id, clone_line,total_line,ratio);
                result.add(repoCloneInfoMonthly);
                last_day_str = commit_date;//更新上一次commit的日期
            }
        }

        return result;
    }


//    //这个commit中，某个开发者的克隆代码行数
//    @Override
//    public DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name) {
//        DeveloperCloneMeasureData developerCloneMeasureData = null;
//        String repoPath=null;
//        try {
//            repoPath=restInterfaceManager.getRepoPath(repo_id,commit_id);
//            if(repoPath != null){
//
//                //#1 获取这个commit的版本的所以克隆实例
//                List<CloneInstanceInfo> lci = cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
//
//                //#2 将克隆实例列表传给方法进行计算，统计得出该开发者的克隆代码行数
//                Integer clone_line =  JGitUtil.getCloneLineByDeveloper(repoPath, commit_id, lci, developer_name);
//                developerCloneMeasureData = new DeveloperCloneMeasureData(repo_id, commit_id, developer_name, clone_line);
//
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            if(repoPath!=null)
//                restInterfaceManager.freeRepoPath(repo_id,repoPath);
//        }
//        return developerCloneMeasureData;
//    }

//    @Override
//    public Set getDeveloperListByRepoId(String repo_id, String commit_id) {
//        Set<String> spi = null;
//        String repoPath=null;
//        try {
//            repoPath=restInterfaceManager.getRepoPath(repo_id,commit_id);
//            spi = JGitUtil.getDeveloperList(repoPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            if(repoPath!=null)
//                restInterfaceManager.freeRepoPath(repo_id,repoPath);
//        }
//
//        return spi;
//
//    }

    /** 
    * @Description:
    * @Param: [repo_id, commit_id, since, until] 
    * @return: java.lang.Integer 
    * @Author: njzhan
    * @Date: 2019-08-23 
    */
//    @Override
//    public RepoCloneMeasureActiveData getCloneActive(String repo_id, String since, String until) {
//        RepoCloneMeasureActiveData repoCloneMeasureActiveData = null;
//        Integer clone_line_num = 0;
//        String repoPath=null;
//
//        try {
//            //#1 根据since 和 until 到issue表内查出对应的issue条目
//            List<Issue> issueList = issueDao.getIssueByDuration(repo_id, since, until);
//            List<RawIssue> rawIssueList = null;
//            //#2 根据since 和 until 得到对应的commit
//            List<Commit> tmp = commitDao.getTwoScannedCommmit(repo_id, since, until);
//            if (tmp.size() == 2){
//                //make sure the tmp size == 2
//                Commit start = tmp.get(0);
//                Commit end = tmp.get(1);
//                String end_commit_id = end.getCommit_id();
//                rawIssueList = rawIssueDao.getRawIssueList(issueList, repo_id, end_commit_id);
//                //根据得到的结尾commit获取repo
//                repoPath=restInterfaceManager.getRepoPath(repo_id,end_commit_id);
//                //在得到的repo上做首尾commit diff
//                //希望得到增加的行号信息
//                FileRepositoryBuilder builder = new FileRepositoryBuilder();
//                Repository repo = builder.setGitDir(new File(repoPath+"/.git")).setMustExist(true).build();
//                Git git = new Git(repo);
//                ObjectId oldId = repo.resolve(start.getCommit_id() + "^{tree}");
//                ObjectId newId = repo.resolve(end.getCommit_id() + "^{tree}");
//
//                ObjectReader reader = repo.newObjectReader();
//                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
//                oldTreeIter.reset(reader, oldId);
//                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
//                newTreeIter.reset(reader, newId);
//
//                List<DiffEntry> diffs= git.diff()
//                        .setNewTree(newTreeIter)
//                        .setOldTree(oldTreeIter)
//                        .call();
//
//                clone_line_num = getAddCloneLine(rawIssueList, diffs, repo);
//                repoCloneMeasureActiveData = new RepoCloneMeasureActiveData(repo_id, start.getCommit_id(), start.getCommit_time(),end.getCommit_id(), end.getCommit_time(), clone_line_num);
//            }
//            else {
//                //can find front and end two commits
//
//                return null;
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(repoPath!=null)
//                restInterfaceManager.freeRepoPath(repo_id,repoPath);
//        }
//        return repoCloneMeasureActiveData;
//    }
//
//    /**
//    * @Description:
//    * @Param: [rawIssueList, diffEntryList, repo]
//    * @return: java.lang.Integer
//    * @Author: njzhan
//    * @Date: 2019-08-28
//    */
//    private Integer getAddCloneLine(List<RawIssue> rawIssueList, List<DiffEntry> diffEntryList, Repository repo){
//        Integer res = 0;//init clone line
//
//        DiffFormatter df = null;
//        // init df
//        df = new DiffFormatter(DisabledOutputStream.INSTANCE);
//        df.setRepository(repo);
//        df.setDiffComparator(RawTextComparator.DEFAULT);
//        df.setDetectRenames(true);
//
//
//        for (DiffEntry entry : diffEntryList) {
//            if(!entry.getNewPath().endsWith(".java")){
//                //如果这个entry不是java文件的 跳过
//                continue;
//            }
//            String edited_file_name = entry.getNewPath();//entry的文件名
//            for(RawIssue rawIssue: rawIssueList){
//                if(rawIssue.getFile_name().equals(edited_file_name)){
//                    //编辑的文件名匹配
//                    try {
//                        for (Edit edit : df.toFileHeader(entry).toEditList()) {
//                            //判断这一块编辑是否是克隆代码
//                            //如果是，增加进总行数内
//                            List<Location> locationList = locationDao.getLocations(rawIssue.getUuid());
//                            for (Location location: locationList){
//                                Integer matchLineNum =  getMatchCloneNum(location, edit);
//                                res += matchLineNum;
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//        return res;
//    }
//
//    private Integer getMatchCloneNum(Location location, Edit edit){
//        Integer res = 0;
//        for(Integer integer = location.getStart_line(); integer <= location.getEnd_line(); integer ++){
//            if (edit.getBeginB() + 1 <= integer &&  integer <= edit.getEndB() + 1){
//                res ++;
//            }
//        }
//        return res;
//    }






}
