/**
 * @description:
 * @author: fancying
 * @create: 2019-04-02 15:27
 **/
package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.SpaceType;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class IssueMeasureInfoServiceImpl implements IssueMeasureInfoService {

    private RawIssueDao rawIssueDao;
    private IssueDao issueDao;
    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void  setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    @Autowired
    public void setIssueDao(IssueDao issueDao) {
        this.issueDao = issueDao;
    }


    @Override
    public int numberOfRemainingIssue(String repoId, String commit, String spaceType, String detail) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return rawIssueDao.getNumberOfRemainingIssue(repoId, commit);
        }
        if (SpaceType.PACKAGE.getLevel().equals(spaceType)) {
            // package name 需要做处理
            detail.replace('.','/');
            return rawIssueDao.getNumberOfRemainingIssueBaseFile(repoId, commit, "%" + detail + "%");
        }
        if (SpaceType.FILE.getLevel().equals(spaceType)) {

            return rawIssueDao.getNumberOfRemainingIssueBaseFile(repoId, commit, "%" + detail);
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
}