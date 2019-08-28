package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Issue;
import cn.edu.fudan.cloneservice.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-26
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@Repository
public class IssueDao {
    @Autowired
    private IssueMapper issueMapper;

    public List<Issue> getIssueByDuration(String repo_id, String start, String end){
        return issueMapper.getIssueByDuration(repo_id, start, end);
    }
//    public Issue getOneIssueByDuration(String repo_id, String start, String end){
//        return issueMapper.getOneIssueByDuration(repo_id, start, end);
//    }
}
