/**
 * @description:
 * @author: fancying
 * @create: 2019-04-08 17:00
 **/
package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.service.IssueRankService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IssueRankServiceImpl implements IssueRankService {
    @Override
    public Map rankOfFile(String repoId, String commit) {
        return null;
    }

    @Override
    public Map rankOfFileBaseDensity(String repoId, String commit) {
        return null;
    }

    @Override
    public Map rankOfDeveloper(String duration, String repoId) {
        return null;
    }

    @Override
    public Map rankOfRepoBaseDensity(String duration, String repoId) {
        return null;
    }
}