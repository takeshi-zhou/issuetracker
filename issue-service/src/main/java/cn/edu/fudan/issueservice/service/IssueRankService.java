package cn.edu.fudan.issueservice.service;

import java.util.Map;

public interface IssueRankService {

    public Map rankOfFile(String repoId, String commit) ;

    public Map rankOfFileBaseDensity(String repoId, String commit) ;

    public Map rankOfDeveloper(String duration,String repoId);

    public Map rankOfRepoBaseDensity(String duration,String repoId) ;
}
