package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.CodeQuality;
import cn.edu.fudan.measureservice.domain.CommitBase;
import cn.edu.fudan.measureservice.domain.CommitInfoDeveloper;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.portrait.Competence;
import cn.edu.fudan.measureservice.portrait.DeveloperMetrics;
import cn.edu.fudan.measureservice.portrait.Efficiency;
import cn.edu.fudan.measureservice.portrait.Quality;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import cn.edu.fudan.measureservice.util.GitUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class MeasureDeveloperServiceImpl implements MeasureDeveloperService {

    private Logger logger = LoggerFactory.getLogger(MeasureDeveloperServiceImpl.class);

    @Value("${repoHome}")
    private String repoHome;
    @Value("${inactive}")
    private int inactive;
    @Value("${lessActive}")
    private int lessActive;
    @Value("${relativelyActive}")
    private int relativelyActive;


    private RestInterfaceManager restInterfaceManager;
    private RepoMeasureMapper repoMeasureMapper;
    private GitUtil gitUtil;


    public MeasureDeveloperServiceImpl(RestInterfaceManager restInterfaceManager, RepoMeasureMapper repoMeasureMapper, GitUtil gitUtil) {
        this.restInterfaceManager = restInterfaceManager;
        this.repoMeasureMapper = repoMeasureMapper;
        this.gitUtil = gitUtil;
    }

    @Override
    public int getCommitCountsByDuration(String repo_id, String since, String until) {
        if (since.compareTo(until)>0 || since.length()>10 || until.length()>10){
            throw new RuntimeException("please input correct date");
        }
        String sinceday = dateFormatChange(since);
        String untilday = dateFormatChange(until);

        int commitCountsByDuration = repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceday, untilday,null);
        return commitCountsByDuration;
    }

    //把日期格式从“2010.10.10转化为2010-10-10”
    private String dateFormatChange(String dateStr){
        String newdateStr = dateStr.replace('.','-');
        return newdateStr;
    }

    @Override
    public String getActivityByRepoId(String repo_id) {
        String active = null;
        LocalDate nowDate = LocalDate.now();
        int nowYear = nowDate.getYear();
        int nowMonth = nowDate.getMonthValue();
        int nowDay = nowDate.getDayOfMonth();
        LocalDate startDate = nowDate.minusDays(90);
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();
        int startDay = startDate.getDayOfMonth();
        String startMonthStr = String.valueOf(startMonth);
        String startDayStr = String.valueOf(startDay);
        String nowMonthStr = String.valueOf(nowMonth);
        String nowDayStr = String.valueOf(nowDay);
        if (startMonth<10){
            startMonthStr = "0" + String.valueOf(startMonth);
        }
        if (startDay<10){
            startDayStr = "0" + String.valueOf(startDay);
        }
        if (nowMonth<10){
            nowMonthStr = "0" + String.valueOf(nowMonth);
        }
        if (nowDay<10){
            nowDayStr = "0" + String.valueOf(nowDay);
        }

        String start = startYear+"."+startMonthStr+"."+startDayStr;
        String now = nowYear+"."+nowMonthStr+"."+nowDayStr;
//        System.out.println(start);
//        System.out.println(now);
        int commitCount = getCommitCountsByDuration(repo_id,start,now);
        if(commitCount != -1){
            if(commitCount <= inactive){
                active = "inactive";
            }else if(commitCount <= lessActive){
                active = "lessActive";
            }else if(commitCount <= relativelyActive){
                active = "relativelyActive";
            }else{
                active = "active";
            }
        }
        return active;
    }

    @Override
    public Object getCodeChangesByDurationAndDeveloperName(String developer_name, String since, String until, String token, String category,String repoId) {
        CommitBase commitBase = new CommitBase();
        int lineAdds = 0;
        int lineDels = 0;
        if(repoId == null || repoId.isEmpty()){
            if(category == null || category.isEmpty()){
                return " The category should not be null when  repo id is null";
            }
            List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String eachRepoId = protectJson.get("repo_id").toString();
                List<RepoMeasure> repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(eachRepoId,developer_name,0,since,until);
                for(RepoMeasure repoMeasure : repoMeasures){
                    lineAdds += repoMeasure.getAdd_lines();
                    lineDels += repoMeasure.getDel_lines();
                }

//                try{
//                    repoPath=restInterfaceManager.getRepoPath(repo_id,"");
//                    if(repoPath!=null){
//
//                        //获取repo一段时间内行数变化值
//                        lineChanges = gitUtil.getRepoLineChanges(repoPath,since,until,developer_name);
//                        lineAdds += lineChanges[0];
//                        lineDels += lineChanges[1];
//
//                    }
//                }finally {
//                    if(repoPath!=null) {
//                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
//                    }
//                }

            }
        }else{
            List<RepoMeasure> repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(repoId,developer_name,0,since,until);
            for(RepoMeasure repoMeasure : repoMeasures){
                lineAdds += repoMeasure.getAdd_lines();
                lineDels += repoMeasure.getDel_lines();
            }
        }

        commitBase.setAddLines(lineAdds);
        commitBase.setDelLines(lineDels);

        return commitBase;
    }

    @Override
    public int getCommitCountByDurationAndDeveloperName(String developerName, String since, String until, String token, String category,String repoId) {
        String repoPath=null;
        int commitTotalCount = 0;
        if(repoId != null && !repoId.isEmpty()){
            return repoMeasureMapper.getCommitCountsByDuration(repoId,since,until,developerName);
        }else{
            List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String repo_id = protectJson.get("repo_id").toString();

                int commitCount = repoMeasureMapper.getCommitCountsByDuration(repo_id,since,until,developerName);
                commitTotalCount += commitCount;

            }
        }

        return commitTotalCount;
    }

    @Override
    public Object getRepoListByDeveloperName(String developer_name, String token, String category) {
        String repoPath=null;
        // 这里配合脚本的判断分支，将since以及until设置为值为null的字符串；
        String since = "null";
        String until = "null";
        int commitCount ;
        Map<String,String> result = new HashMap<>();
        List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
        if(projectList != null){
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String repo_id = protectJson.get("repo_id").toString();
                String repo_name = protectJson.get("name").toString();
                try{
                    repoPath=restInterfaceManager.getRepoPath(repo_id,"");
                    if(repoPath!=null){
                        commitCount = gitUtil.getCommitCount(repoPath,since,until,developer_name);
                        if(commitCount != 0){
                            String active = getActivityByRepoId(repo_id);
                            result.put(repo_name,active);
                        }
                    }
                }finally {
                    if(repoPath!=null) {
                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Object getQualityChangesByDeveloperName(String developer_name, String token, String category, int counts, String project_name) {
        String spaceType= "project";
        //判断count是否合法
        if(counts<0){
            throw new RuntimeException("counts can not be  negative");
        }

        List<RepoMeasure> repoMeasures;
        //如果projectName的名字为null则表示用户在所有project列表中最近30次commit的代码质量信息
        if(project_name==null){
            repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(null,developer_name,counts,null,null);
        }else{
            JSONObject result = restInterfaceManager.getProjectListByCondition(token,category,project_name,null);
            int code =result.getIntValue("code");
            if(code != 200){
                logger.error("request project api failed  ---> {}",project_name);
                throw new RuntimeException("request project api failed  ---> "+ project_name);
            }
            JSONArray projects = result.getJSONArray("data");
            if(projects.size() == 0){
                logger.info("do not have this project --> {}",project_name );
                throw new RuntimeException("do not have this project --> "+project_name);
            }
            if (projects.size() > 1){
                // 这一段的方法待优化 可以在多个project中取最近counts 次数的commits的度量
                logger.info("more than one project named --> {}",project_name );
                throw new RuntimeException("more than one project named --> "+project_name);
            }
            JSONObject project =  projects.getJSONObject(0);
            String repoId = project.getString("repo_id");
            repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(repoId,developer_name,counts,null,null);

        }


        List<CodeQuality> queries = new ArrayList<>();

        CodeQuality codeQuality;
        String projectName = null;
        for(RepoMeasure repoMeasure :repoMeasures){
            int newIssueCounts = restInterfaceManager.getNumberOfNewIssueByCommit(repoMeasure.getRepo_id(),repoMeasure.getCommit_id(),category,spaceType,token);
            codeQuality = new CodeQuality(repoMeasure.getAdd_lines(),repoMeasure.getDel_lines(),newIssueCounts,repoMeasure.getCommit_time());
            JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoMeasure.getRepo_id());
            if(projects.size()==0){
                logger.error("can not find project by repo_id --->{}",repoMeasure.getRepo_id());
            }else {
                projectName = projects.getJSONObject(0).getString("name");
            }
            codeQuality.setCommitId(repoMeasure.getCommit_id());
            codeQuality.setProjectName(projectName);
            if(newIssueCounts == -1){
                logger.info("this commit --> {} can not be compiled",repoMeasure.getCommit_id());
                codeQuality.setExpression("this commit can not be compiled!");
            }

            queries.add(codeQuality);
        }

        return queries;
    }

    @Override
    public Object getDeveloperActivenessByDuration(String repo_id, String since, String until, String developer_name) {
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
            Map<String, Object> map = new HashMap<>();

            List<CommitInfoDeveloper> CommitInfoDeveloper = repoMeasureMapper.getCommitInfoDeveloperListByDuration(repo_id, indexDay.toString(), indexDay.toString(), developer_name);
            if (CommitInfoDeveloper.size()==1){
                int add = CommitInfoDeveloper.get(0).getAdd();
                int del = CommitInfoDeveloper.get(0).getDel();
                //还差缺陷数量，计算E/L N/L
//                int newIssues = 0;
//                int elliminateIssues = 0;

//                double newIssuesQuality = newIssues*100.0 / (add+del);
//                double elliminateIssuesQuality = elliminateIssues*100.0 / (add+del);

                map.put("commit_date", indexDay.toString());
                map.put("add", add);
                map.put("del", del);
//                map.put("E/L", newIssuesQuality);
                result.add(map);
            }

            indexDay = indexDay.plusDays(1);
        }
        return result;

    }

    @Override
    public Object getDeveloperActivenessByGranularity(String repo_id, String granularity, String developer_name) {
        //获取查询时的日期 也就是今天的日期,也作为查询时间段中的截止日期
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();


        //此为“week”的情况
        LocalDate sinceDay = today.minusWeeks(1);
        String since = sinceDay.toString();
        String until = today.toString();

        switch (granularity){
            case "week":
                return getDeveloperActivenessByDuration(repo_id, since, until, developer_name);
            case "month":
                sinceDay = today.minusMonths(1);
                since = sinceDay.toString();
                return getDeveloperActivenessByDuration(repo_id, since, until, developer_name);
            case "year":
                sinceDay = today.minusYears(1);
                LocalDate indexDay = sinceDay;
                while(today.isAfter(indexDay)){
                    Map<String, Object> map = new HashMap<>();
                    List<CommitInfoDeveloper> list = repoMeasureMapper.getCommitInfoDeveloperListByDuration(repo_id, indexDay.toString(), indexDay.plusMonths(1).toString(), developer_name);

                    if (list.size()>0){
                        int add = list.get(0).getAdd();
                        int del = list.get(0).getDel();
                        map.put("commit_date", indexDay.toString().substring(0,7));
                        map.put("add", add);
                        map.put("del", del);
                        result.add(map);
                    }

                    //indexDay 变成indexDay这天的下个月的这一天
                    indexDay = indexDay.plusMonths(1);
                }
                break;
            default:
                throw new RuntimeException("please input correct granularity type: week or month or year");
        }
        return result;

    }

    @Override
    public Object getPortrait(String repoId, String developer, String beginDate, String endDate, String token, String tool) {
        //--------------------------以下是开发效率相关指标-----------------------------
        Efficiency efficiency = new Efficiency();

        //提交频率指标
        int totalCommitCount = getCommitCountsByDuration(repoId, beginDate, endDate);
        int developerCommitCount = repoMeasureMapper.getCommitCountsByDuration(repoId, beginDate, endDate, developer);
        double commitFrequency = -1;
        if (totalCommitCount != 0){
            commitFrequency = developerCommitCount*(1.0)/totalCommitCount;
        }
        efficiency.setCommitFrequency(commitFrequency);

        //代码量指标
        int developerLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, developer);
        int totalLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, "");
        double workLoad = -1;
        if (totalLOC != 0){
            workLoad = developerLOC*(1.0)/totalLOC;
        }
        efficiency.setWorkLoad(workLoad);

        //获取代码新增、删除逻辑行数数据
        JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
        String branch = projects.getJSONObject(0).getString("branch");
        JSONObject statements = restInterfaceManager.getStatements(repoId, beginDate, endDate, branch);
        int developerAddStatement = 0;
        int totalAddStatement = 0;
        int developerDelStatement = 0;
        int totalDelStatement = 0;
        for(String str:statements.keySet()){
            if (str.equals(developer)){
                developerAddStatement = statements.getJSONObject(str).getIntValue("ADD");
                developerDelStatement = statements.getJSONObject(str).getIntValue("DELETE");
            }
            totalAddStatement += statements.getJSONObject(str).getIntValue("ADD");
            totalDelStatement += statements.getJSONObject(str).getIntValue("DELETE");
        }
        //新增逻辑行指标
        efficiency.setNewLogicLine((totalAddStatement == 0) ? -1 : developerAddStatement*(1.0)/totalAddStatement);
        //删除逻辑行指标
        efficiency.setDelLogicLine((totalDelStatement == 0) ? -1 : developerDelStatement*(1.0)/totalDelStatement);

        JSONObject validLines = restInterfaceManager.getValidLine(repoId, beginDate, endDate, branch);
        int developerValidLine = 0;
        int totalValidLine = 0;
        for(String key:validLines.keySet()){
            if (key.equals(developer)){
                developerValidLine = validLines.getIntValue(key);
            }
            totalValidLine += validLines.getIntValue(key);
        }
        //有效代码行指标
        efficiency.setValidStatement((totalValidLine == 0) ? -1 : developerValidLine*(1.0)/totalValidLine);
//
        //----------------------------------以下是代码质量相关指标-------------------------------------
        //个人规范类issue数
        int developerStandardIssueCount = restInterfaceManager.getIssueCountByConditions(developer, repoId, beginDate, endDate, tool, "standard", token);
        //个人安全类issue数
        int developerSecurityIssueCount = restInterfaceManager.getIssueCountByConditions(developer, repoId, beginDate, endDate, tool, "security", token);
        //repo总issue数
        int totalIssueCount = restInterfaceManager.getIssueCountByConditions("", repoId, beginDate, endDate, tool, "", token);
        JSONArray issueList = restInterfaceManager.getNewElmIssueCount(repoId, beginDate, endDate, tool, token);
        int developerNewIssueCount = 0;//个人新增缺陷数
        int totalNewIssueCount = 0;//总新增缺陷数
        for (int i = 0; i < issueList.size(); i++){
            JSONObject each = issueList.getJSONObject(i);
            String developerName = each.getString("developer");
            int newIssueCount = each.getIntValue("newIssueCount");
            if (developer.equals(developerName)){
                developerNewIssueCount = newIssueCount;
            }
            totalNewIssueCount += newIssueCount;
        }

        Quality quality = new Quality();
        //规范性指标
        double standardScore = 1; //分母为0时也返回1
        if (totalIssueCount != 0){
            standardScore = developerStandardIssueCount*(1.0)/totalIssueCount == 0 ? 1 : developerStandardIssueCount*(1.0)/totalIssueCount;
        }
        //安全性指标
        double securityScore = 1;//分母为0时返回1
        if (totalIssueCount != 0){
            securityScore = developerSecurityIssueCount*(1.0)/totalIssueCount == 0 ? 1 : developerSecurityIssueCount*(1.0)/totalIssueCount;
        }
        //缺陷率指标
        double issueRate = 1;//分母为0时返回1
        if (totalNewIssueCount != 0){
            issueRate = developerNewIssueCount*(1.0)/totalNewIssueCount == 0 ? 1 : developerNewIssueCount*(1.0)/totalNewIssueCount;
        }
        //总体问题密度指标
        double issueDensity = -1;//分母为0时返回-1
        if (totalLOC != 0){
            issueDensity = developerNewIssueCount*(1.0)/totalLOC == 0 ? 1 : developerNewIssueCount*(1.0)/totalLOC;
        }

        quality.setStandard(standardScore);
        quality.setSecurity(securityScore);
        quality.setIssueRate(issueRate);
        quality.setIssueDensity(issueDensity);

        //----------------------------------开发能力相关指标-------------------------------------
        int developerAddLine = repoMeasureMapper.getAddLinesByDuration(repoId, beginDate, endDate, developer);
        JSONObject cloneMeasure = restInterfaceManager.getCloneMeasure(repoId, developer, beginDate, endDate);
        int increasedCloneLines = Integer.parseInt(cloneMeasure.getString("increasedCloneLines"));
        int selfIncreasedCloneLines = Integer.parseInt(cloneMeasure.getString("selfIncreasedCloneLines"));
        int eliminateCloneLines = Integer.parseInt(cloneMeasure.getString("eliminateCloneLines"));
        int allEliminateCloneLines = Integer.parseInt(cloneMeasure.getString("allEliminateCloneLines"));
        JSONObject focusMeasure = restInterfaceManager.getFocusFilesCount(repoId, beginDate, endDate);
        int totalChangedFile = focusMeasure.getIntValue("total");
        int developerFocusFile = focusMeasure.getJSONObject("developer").getIntValue(developer);
        JSONObject changedCodeInfo = restInterfaceManager.getChangedCodeAge(repoId, beginDate, endDate, developer);
        int changedCodeAVGAge = changedCodeInfo.getIntValue("average");
        int changedCodeMAXAge = changedCodeInfo.getIntValue("max");
        JSONObject deletedCodeInfo = restInterfaceManager.getDeletedCodeAge(repoId, beginDate, endDate, developer);
        int deletedCodeAVGAge = deletedCodeInfo.getIntValue("average");
        int deletedCodeMAXAge = deletedCodeInfo.getIntValue("max");
        int repoAge = restInterfaceManager.getRepoAge(repoId, endDate);

        Competence competence = new Competence();
        double nonRepetitiveCodeRate = -1;
        if (developerAddLine != 0){
            nonRepetitiveCodeRate = (developerAddLine - increasedCloneLines)*(1.0)/developerAddLine;
        }

        double nonSelfRepetitiveCodeRate = -1;
        if (developerAddLine != 0){
            nonSelfRepetitiveCodeRate = (developerAddLine - selfIncreasedCloneLines)*(1.0)/developerAddLine;
        }

        double focusRange = -1;
        if (totalChangedFile != 0){
            focusRange = (developerFocusFile)*(1.0)/totalChangedFile;
        }

        double eliminateDuplicateCodeRate = -1;
        if (allEliminateCloneLines != 0){
            eliminateDuplicateCodeRate = (eliminateCloneLines)*(1.0)/allEliminateCloneLines;
        }

        double oldCodeModification = -1;
        if (repoAge != 0){
            oldCodeModification = (changedCodeAVGAge + deletedCodeAVGAge)*(1.0)/repoAge;
        }


        competence.setNonRepetitiveCodeRate(nonRepetitiveCodeRate);
        competence.setNonSelfRepetitiveCodeRate(nonSelfRepetitiveCodeRate);
        competence.setFocusRange(focusRange);
        competence.setEliminateDuplicateCodeRate(eliminateDuplicateCodeRate);
        competence.setOldCodeModification(oldCodeModification);
        competence.setChangedCodeMAXAge(changedCodeMAXAge);
        competence.setDeletedCodeMAXAge(deletedCodeMAXAge);

        return new DeveloperMetrics(developer, efficiency, quality, competence);
    }

    @Override
    public Object getLOCByCondition(String repoId, String developer, String beginDate, String endDate, String type) {
        int totalLOC = repoMeasureMapper.getLOCByCondition(repoId,developer,beginDate,endDate);
        if (type.equals("total")){
            return totalLOC;
        }
        if (type.equals("dayAverage")){
            List<Map<String, Object>> commitDays = repoMeasureMapper.getCommitDays(repoId,developer,beginDate,endDate);
            int days = commitDays.size();
            return totalLOC/days;
        }
        return ("please input correct type: total or dayAverage !");
    }
}
