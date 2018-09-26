package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.CloneRawIssue;
import cn.edu.fudan.cloneservice.mapper.CloneRawIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Repository
public class CloneRawIssueDao {

    private CloneRawIssueMapper cloneRawIssueMapper;

    @Autowired
    public CloneRawIssueDao(CloneRawIssueMapper cloneRawIssueMapper) {
        this.cloneRawIssueMapper = cloneRawIssueMapper;
    }

    public void insertCloneRawIssueList(List<CloneRawIssue> list){
        cloneRawIssueMapper.insertCloneRawIssueList(list);
    }

    public List<CloneRawIssue> getCloneRawIssueWithLocations(String commit_id){
        return cloneRawIssueMapper.getCloneRawIssueWithLocations(commit_id);
    }
}
