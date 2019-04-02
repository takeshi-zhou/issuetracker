/**
 * @description:
 * @author: fancying
 * @create: 2019-04-02 15:27
 **/
package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.SpaceType;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IssueMeasureInfoServiceImpl implements IssueMeasureInfoService {

    private RawIssueDao rawIssueDao;

    @Autowired
    public void  setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
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
            return rawIssueDao.getNumberOfRemainingIssueBaseFile(repoId, commit, detail);
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

/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return ;
        }*/
        String start ;
        String end;

/*        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return rawIssueDao.numberOfNewIssue(time, detail);
        }*/


        return -1;
    }

    @Override
    public int numberOfEliminateIssue(String duration, String spaceType, String detail) {
        return -1;
    }
}