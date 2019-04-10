package cn.edu.fudan.issueservice.service;

import java.util.Map;

public interface IssueRankService {

    //参与开发者最多的文件排名
    Map rankOfFileBaseDeveloperQuantity(String repoId, String duration, String spaceType, String detail) ;

    //问题最多的文件排名
    Object rankOfFileBaseIssueQuantity(String repoId, String commitId) ;

    //问题密度最大的文件排名
    Map rankOfFileBaseDensity(String repoId, String commitId) ;

    //问题密度最大的开发者排名
    Map rankOfDeveloper(String repoId, String duration,String developerId);

    //问题密度最大（小）的项目排名
    Map rankOfRepoBaseDensity(String repoId, String duration) ;
}
