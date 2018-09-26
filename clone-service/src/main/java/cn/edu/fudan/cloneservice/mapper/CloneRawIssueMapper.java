package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneRawIssue;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloneRawIssueMapper {

    void insertCloneRawIssueList(List<CloneRawIssue> list);
}
