/**
 * @description:
 * @author: fancying
 * @create: 2019-04-02 15:27
 **/
package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.dao.ScanResultDao;
import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class IssueMeasureInfoServiceImpl implements IssueMeasureInfoService {

    private RawIssueDao rawIssueDao;
    private IssueDao issueDao;
    private RestInterfaceManager restInterfaceManager;
    private ScanResultDao scanResultDao;

    public IssueMeasureInfoServiceImpl(RawIssueDao rawIssueDao,
                                       IssueDao issueDao,
                                       RestInterfaceManager restInterfaceManager,
                                       ScanResultDao scanResultDao) {
        this.rawIssueDao = rawIssueDao;
        this.issueDao = issueDao;
        this.restInterfaceManager = restInterfaceManager;
        this.scanResultDao = scanResultDao;
    }

    @Override
    public int numberOfRemainingIssue(String repoId, String commit, String spaceType, String detail) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return rawIssueDao.getNumberOfRemainingIssue(repoId, commit);
        }
        if (SpaceType.PACKAGE.getLevel().equals(spaceType)) {

            // package name 需要做处理
            return rawIssueDao.getNumberOfRemainingIssueBasePackage(repoId, commit,
                    "%" + detail.replace('.','/') + "%");
        }
        if (SpaceType.FILE.getLevel().equals(spaceType)) {

            return rawIssueDao.getNumberOfRemainingIssueBaseFile(repoId, commit, detail);
        }
        // 需要单独引入用户问题记录表 ？
/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return rawIssueDao.getNumberOfRemainingIssueBaseDeveloper(repoId, commit, detail);
        }*/

        return -1;
    }




    @Override
    public int numberOfNewIssue(String duration, String spaceType, String detail) {
        // duration: 2018.01.01-2018.12.12
        if (duration.length() < 21)
            throw new RuntimeException("duration error!");
        String start = duration.substring(0,10);
        String end = duration.substring(11,21);
        //List<String> commits = restInterfaceManager.getScanCommitsIdByDuration(detail, start, end);

/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return ;
        }*/

        // detail 是repoId
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return issueDao.getNumberOfNewIssueByDuration(detail, start, end);
        }


        return -1;
    }

    @Override
    public int numberOfNewIssueByCommit(String repoId, String commitId, String spaceType,String category) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return scanResultDao.getNumberOfNewIssueByCommit(repoId, commitId,category);
        }
        return -1;
    }

    @Override
    public int numberOfEliminateIssue(String duration, String spaceType, String detail) {
        // duration: 2018.01.01-2018.12.12
        if (duration.length() < 21)
            throw new RuntimeException("duration error!");
        String start = duration.substring(0,10);
        String end = duration.substring(11,21);

        // detail 是repoId
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return issueDao.getNumberOfEliminateIssueByDuration(detail, start, end);
        }
/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return ;
        }*/

        return -1;
    }

    @Override
    public int numberOfEliminateIssueByCommit(String repoId, String commitId, String spaceType,String category) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return scanResultDao.getNumberOfEliminateIssueByCommit(repoId, commitId,category);
        }
        return -1;
    }

    @Override
    public List<IssueCountPo> getIssueCountEachCommit(String repoId,String category, String since, String until) {
        return scanResultDao.getScanResultsEachCommit(repoId,category,since,until);
    }

    @Override
    public IssueCountMeasure getIssueCountMeasureByRepo(String repoId, String category, String since, String until) {
        IssueCountMeasure issueCountMeasure=new IssueCountMeasure();
        issueCountMeasure.setNewIssueCount(issueDao.getNumberOfNewIssueByDuration(repoId, since, until));
        issueCountMeasure.setEliminatedIssueCount(issueDao.getNumberOfEliminateIssueByDuration(repoId, since, until));
        return issueCountMeasure;
    }

    @Override
    public List<IssueCountDeveloper> getIssueCountMeasureByDeveloper(String repoId, String category, String since, String until) {
        return scanResultDao.getScanResultsEachDeveloper(repoId, category, since, until);
    }

    @Override
    public Object getNotSolvedIssueCountByCategoryAndRepoId(String repoId, String category,String commitId) {
        Map<String,Integer> issueCount = new HashMap<>();
        Map<String,Integer> result = new LinkedHashMap<>();

        if(commitId == null){
            List<Issue> issues = issueDao.getNotSolvedIssueAllListByCategoryAndRepoId(repoId,category);

            for (Issue issue:
                    issues) {
                String issueType = issue.getType();
                int count = issueCount.get(issueType) != null ? issueCount.get(issueType) : 0;
                issueCount.put(issueType,++count);
            }
        }else{
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId,category,commitId);
            for (RawIssue rawIssue:
                    rawIssues) {
                String rawIssueType = rawIssue.getType();
                int count = issueCount.get(rawIssueType) != null ? issueCount.get(rawIssueType) : 0;
                issueCount.put(rawIssueType,++count);
            }
        }

        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(issueCount.entrySet());
        Collections.sort(list,(o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        for(Map.Entry<String,Integer> entry : list){
            result.put(entry.getKey(),entry.getValue());
        }

        return result;
    }
}