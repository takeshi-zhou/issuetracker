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
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.gitrepo.RepoCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;



@Service
public class CloneMeasureServiceImpl implements CloneMeasureService {
    @Autowired
    CloneInstanceInfoDao cloneInstanceInfoDao;

    @Autowired
    RepoCommitMapper repoCommitMapper;

    @Autowired
    IssueDao issueDao;

    @Autowired
    RawIssueDao rawIssueDao;

    @Autowired
    CommitDao commitDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    RepoMeasureDao repoMeasureDao;

    @Autowired
    private RestInterfaceManager restInterfaceManager;
    @Value("${repoHome}")
    private String repoHome;

    /**
     * 这个commit中，某个开发者的克隆代码行数
     * @param repoId
     * @param commitId
     * @param developerName
     * @return
     */
    @Override
    public DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repoId, String commitId, String developerName) {
        DeveloperCloneMeasureData developerCloneMeasureData = null;
        String repoPath=null;
        try {
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);
            if(repoPath != null){

                //#1 获取这个commit的版本的所有克隆实例
                List<CloneInstanceInfo> lci = cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repoId, commitId);

                //#2 将克隆实例列表传给方法进行计算，统计得出该开发者的克隆代码行数
                Integer cloneLine =  JGitUtil.getCloneLineByDeveloper(repoPath, commitId, lci, developerName);
                developerCloneMeasureData = new DeveloperCloneMeasureData(repoId, commitId, developerName, cloneLine);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        return developerCloneMeasureData;
    }

    @Override
    public Set getDeveloperListByRepoId(String repoId, String commitId) {
        Set<String> spi = null;
        String repoPath=null;
        try {
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);
            spi = JGitUtil.getDeveloperList(repoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }

        return spi;

    }

    @Override
    public RepoCloneMeasureActiveData getCloneActive(String repoId, String since, String until) {
        RepoCloneMeasureActiveData repoCloneMeasureActiveData = null;
        Integer cloneLineNum = 0;
        String repoPath=null;

        try {
            //#1 根据since 和 until 到issue表内查出对应的issue条目
            List<Issue> issueList = issueDao.getIssueByDuration(repoId, since, until);
            List<RawIssue> rawIssueList = null;
            //#2 根据since 和 until 得到对应的commit
            List<Commit> tmp = commitDao.getTwoScannedCommmit(repoId, since, until);
            if (tmp.size() == 2){
                //make sure the tmp size == 2
                Commit start = tmp.get(0);
                Commit end = tmp.get(1);
                String endCommitId = end.getCommit_id();
                rawIssueList = rawIssueDao.getRawIssueList(issueList, repoId, endCommitId);
                //根据得到的结尾commit获取repo
                repoPath=restInterfaceManager.getRepoPath(repoId,endCommitId);
                //在得到的repo上做首尾commit diff
                //希望得到增加的行号信息
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                Repository repo = builder.setGitDir(new File(repoPath+"/.git")).setMustExist(true).build();
                Git git = new Git(repo);
                ObjectId oldId = repo.resolve(start.getCommit_id() + "^{tree}");
                ObjectId newId = repo.resolve(end.getCommit_id() + "^{tree}");

                ObjectReader reader = repo.newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldId);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, newId);

                List<DiffEntry> diffs= git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();

                cloneLineNum = getAddCloneLine(rawIssueList, diffs, repo);
                repoCloneMeasureActiveData = new RepoCloneMeasureActiveData(repoId, start.getCommit_id(), start.getCommit_time(),end.getCommit_id(), end.getCommit_time(), cloneLineNum);
            }
            else {
                //can find front and end two commits

                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        return repoCloneMeasureActiveData;
    }

    @Override
    public CloneMeasure getCloneMeasure(String repoId, String commitId){

        CloneMeasure cloneMeasure = new CloneMeasure();

        int increasedCloneLines = 0;
        int increasedSameCloneLines = 0;
        int increasedLines = 0;
        String repoPath=null;
        Map<String, String> map;
        try {
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);

            CommitChange commitChange = JGitUtil.getNewlyIncreasedLines(repoPath, commitId);

            map = commitChange.getAddMap();
            increasedLines = commitChange.getAddLines();
            List<CloneLocation> cloneLocations = locationDao.getCloneLocations(repoId, commitId);

            Map<String, List<CloneLocation>> cloneLocationMap = new HashMap<>();

            //初始化
            for(CloneLocation cloneLocation : cloneLocations){
                String type = cloneLocation.getType();
                if(cloneLocationMap.containsKey(type)){
                    cloneLocationMap.get(type).add(cloneLocation);
                }else {
                    List<CloneLocation> locations = new ArrayList<>();
                    locations.add(cloneLocation);
                    cloneLocationMap.put(type, locations);
                }
            }

            for(CloneLocation cloneLocation : cloneLocations){
                String cloneLines = cloneLocation.getBugLines();
                int startLine = Integer.parseInt(cloneLines.split(",")[0]);
                int endLine = Integer.parseInt(cloneLines.split(",")[1]);
                for(String filePath: map.keySet()){
                    if(filePath.equals(cloneLocation.getFilePath())){

                        String[] lines = map.get(filePath).split(",");

                        for(int i = 0; i < lines.length; i++){
                            if(Integer.parseInt(lines[i]) >= startLine && Integer.parseInt(lines[i]) <= endLine){
                                List<CloneLocation> list = cloneLocationMap.get(cloneLocation.getType());
                                list.remove(cloneLocation);
                                for(CloneLocation cloneLocation1 : list){
                                    String filePath1 = cloneLocation1.getFilePath();
                                    String cloneLines1 = cloneLocation1.getBugLines();
                                    if(JGitUtil.isSameDeveloperClone(repoPath, commitId, filePath1, cloneLines1)){
                                        increasedSameCloneLines++;
                                        //跳出for循环
                                        break;
                                    }
                                }
                                increasedCloneLines++;
                            }

                        }
                        //移除检测过的文件
                        map.remove(filePath);
                        //跳出for循环
                        break;
                    }
                }
            }
            cloneMeasure.setIncreasedCloneLines(increasedCloneLines);
            cloneMeasure.setIncreasedCloneLinesRate((double) increasedCloneLines/(double) increasedLines);
            cloneMeasure.setSelfIncreasedCloneLines(increasedSameCloneLines);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }

        return cloneMeasure;
    }


    private Integer getAddCloneLine(List<RawIssue> rawIssueList, List<DiffEntry> diffEntryList, Repository repo){
        //init clone line
        Integer res = 0;

        DiffFormatter df = null;
        // init df
        df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repo);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);


        for (DiffEntry entry : diffEntryList) {
            if(!entry.getNewPath().endsWith(".java")){
                //如果这个entry不是java文件的 跳过
                continue;
            }
            //entry的文件名
            String editedFileName = entry.getNewPath();
            for(RawIssue rawIssue: rawIssueList){
                if(rawIssue.getFile_name().equals(editedFileName)){
                    //编辑的文件名匹配
                    try {
                        for (Edit edit : df.toFileHeader(entry).toEditList()) {
                            //判断这一块编辑是否是克隆代码
                            //如果是，增加进总行数内
                            List<Location> locationList = locationDao.getLocations(rawIssue.getUuid());
                            for (Location location: locationList){
                                Integer matchLineNum =  getMatchCloneNum(location, edit);
                                res += matchLineNum;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return res;
    }

    private int getMatchCloneNum(Location location, Edit edit){
        int res = 0;
        String cloneLines = location.getBug_lines();
        int startLine = Integer.parseInt(cloneLines.split(",")[0]);
        int endLine = Integer.parseInt(cloneLines.split(",")[1]);

        for(int i = startLine; i <= endLine; i++){
            if (edit.getBeginB() + 1 <= i &&  i <= edit.getEndB()){
                res ++;
            }
        }
        return res;
    }

}
