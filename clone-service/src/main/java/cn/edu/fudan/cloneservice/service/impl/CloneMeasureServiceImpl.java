package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.*;
import cn.edu.fudan.cloneservice.domain.*;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
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

//    @Autowired
//    private RestInterfaceManager restInterfaceManager;
    @Value("${repoHome}")
    private String repoHome;

    private Integer getCloneLine(List<CloneInstanceInfo> lci){
        Integer res = 0;
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
        Integer sum =  getCloneLine(lci);
        RepoCloneMeasureData repoCloneMeasureData = new RepoCloneMeasureData(repo_id, commit_id, sum);

        return repoCloneMeasureData;
    }

    @Override
    public RepoCloneRatio getRepoCloneRatioByRepoIdCommitId(String repo_id, String commit_id) {
        List<CloneInstanceInfo> lci =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
        Integer clone_line = 0;
        for (CloneInstanceInfo cloneInstanceInfo: lci){
            clone_line += (cloneInstanceInfo.getEnd_line() - cloneInstanceInfo.getStart_line() + 1);
        }
        //get total line
        Integer total_line = repoMeasureDao.getTotalLineByRepoIdCommitId(repo_id, commit_id);
        Double ratio = 1.0 * clone_line / total_line;
        return new RepoCloneRatio(repo_id, commit_id, ratio);
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
