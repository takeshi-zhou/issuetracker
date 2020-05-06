package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Issue;
import cn.edu.fudan.cloneservice.domain.RawIssue;
import cn.edu.fudan.cloneservice.mapper.RawIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** 
* @Description:
* @Author: njzhan
* @Date: 2019-08-27 
*/
@Repository
public class RawIssueDao {
    @Autowired
    RawIssueMapper rawIssueMapper;

    public RawIssue getRawIssueByUUId(String uuid){
        return rawIssueMapper.getRawIssueByUUID(uuid);
    }

    public List<RawIssue> getRawIssueList(List<Issue> issueList, String repo_id, String commit_id){
        List<RawIssue> result_list = new ArrayList<>();
        for (Issue issue: issueList){
            RawIssue rawIssue= rawIssueMapper.getRawIssueByRepoIdCommitIdIssueId(repo_id, commit_id, issue.getUuid());
            if(rawIssue != null){
                result_list.add(rawIssue);
            }
        }
        return result_list;
    }

    public List<RawIssue> getRawIssueByTypeAndCommit(String repoId, String commitId, String type){
        return rawIssueMapper.getRawIssueByTypeAndCommit(repoId, commitId, type);
    }

    public List<RawIssue> getRawIssuesByCommitId(String repoId, String commitId){
        return rawIssueMapper.getRawIssueByCommitId(repoId, commitId);
    }

}
