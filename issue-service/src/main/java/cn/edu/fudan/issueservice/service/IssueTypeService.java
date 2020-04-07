package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.domain.IssueType;

import java.util.List;

public interface IssueTypeService {

    void insertIssueList(List<IssueType> list);

    IssueType getIssueTypeByTypeName(String type);
}
