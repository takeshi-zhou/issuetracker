/**
 * @description:
 * @author: fancying
 * @create: 2019-04-08 17:00
 **/
package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.service.IssueRankService;
import cn.edu.fudan.issueservice.util.ExecuteShellUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IssueRankServiceImpl implements IssueRankService {

    private RawIssueDao rawIssueDao;
    private ExecuteShellUtil executeShellUtil;
    private RestInterfaceManager restInterfaceManager;


    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    @Autowired
    public void setExecuteShellUtil(ExecuteShellUtil executeShellUtil) {
        this.executeShellUtil = executeShellUtil;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    // 参与开发者最多的文件排名（？）	周，月	文件，项目	看项目的参与者的多少 ???? 单独放在另外的服务中
    @Override
    public Map rankOfFileBaseDeveloperQuantity(String repoId, String duration, String spaceType, String detail) {

        return null;
    }

    @Override
    public Object rankOfFileBaseIssueQuantity(String repoId, String commitId) {
        Map<String, String> map = new WeakHashMap<>();
        for (Map<String, String> m : rawIssueDao.getRankOfFileBaseIssueQuantity(repoId, commitId)) {
            map.put(m.get("key"), m.get("value"));
        }
        return map;
    }


    // ？？？？ 需要代码行数 需要提前入库
    @Override
    public Map rankOfFileBaseDensity(String repoId, String commitId) {
        Map<String, String> map = new WeakHashMap<>();
        for (Map<String, String> m : rawIssueDao.getRankOfFileBaseIssueQuantity(repoId, commitId)) {
            map.put(m.get("key"), m.get("value"));
        }
        return map;
    }

    // 开发人员在某段时间内贡献的代码行数 除以 产生的新Issue数量
    // 平均每多少行代码会产生一个新的Issue
    @Override
    public Map rankOfDeveloper(String repoId, String duration, String developerId) {
        // duration: 2018.01.01-2018.12.12
        if (duration.length() < 21)
            throw new RuntimeException("duration error!");
        String start = duration.substring(0,10);
        String end = duration.substring(11,21);
        String repoPath = restInterfaceManager.getRepoPath(repoId);
        List<String> developers = restInterfaceManager.getDevelopers(repoId);
        // 开发人员 代码行数
        Map<String,Integer> usersCodeLine  = executeShellUtil.developersLinesOfCode(start, end , repoPath, developers);
        // 开发人员 Issue数量

       // List<Integer> issueNumber =

        return usersCodeLine;
    }

    @Override
    public Map rankOfRepoBaseDensity(String repoId, String duration) {
        return null;
    }
}