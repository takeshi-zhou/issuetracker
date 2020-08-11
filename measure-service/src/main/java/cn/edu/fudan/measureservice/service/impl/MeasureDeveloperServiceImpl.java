package cn.edu.fudan.measureservice.service.impl;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.CodeQuality;
import cn.edu.fudan.measureservice.domain.CommitBase;
import cn.edu.fudan.measureservice.domain.CommitInfoDeveloper;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.portrait.*;
import cn.edu.fudan.measureservice.portrait2.Contribution;
import cn.edu.fudan.measureservice.service.MeasureDeveloperService;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
//@CacheConfig(cacheNames = "portrait")
public class MeasureDeveloperServiceImpl implements MeasureDeveloperService {

    @Value("${inactive}")
    private int inactive;
    @Value("${lessActive}")
    private int lessActive;
    @Value("${relativelyActive}")
    private int relativelyActive;


    private RestInterfaceManager restInterfaceManager;
    private RepoMeasureMapper repoMeasureMapper;


    public MeasureDeveloperServiceImpl(RestInterfaceManager restInterfaceManager, RepoMeasureMapper repoMeasureMapper) {
        this.restInterfaceManager = restInterfaceManager;
        this.repoMeasureMapper = repoMeasureMapper;
    }

    @Override
    public Object getWorkLoadOfOneDeveloper(String developer, String since, String until, String repoId) {
        List<Map<String, Object>> workLoadList = repoMeasureMapper.getWorkLoadByCondition(repoId, developer, since, until);

        return workLoadList;
    }

    @Override
    public int getCommitCountsByDuration(String repo_id, String since, String until) {
        if (since.compareTo(until)>0 || since.length()>10 || until.length()>10){
            throw new RuntimeException("please input correct date");
        }
        String sinceday = dateFormatChange(since);
        String untilday = dateFormatChange(until);

        return repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceday, untilday,null);
    }

    //把日期格式从“2010.10.10转化为2010-10-10”
    private String dateFormatChange(String dateStr){
        return dateStr.replace('.','-');
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
            startMonthStr = "0" + startMonth;
        }
        if (startDay<10){
            startDayStr = "0" + startDay;
        }
        if (nowMonth<10){
            nowMonthStr = "0" + nowMonth;
        }
        if (nowDay<10){
            nowDayStr = "0" + nowDay;
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
//        String repoPath=null;
//        // 这里配合脚本的判断分支，将since以及until设置为值为null的字符串；
//        String since = "null";
//        String until = "null";
//        int commitCount ;
//        Map<String,String> result = new HashMap<>();
//        List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
//        if(projectList != null){
//            for(Object project:projectList){
//                JSONObject protectJson = (JSONObject)project;
//                if(protectJson.get("download_status").toString().equals("Downloading")){
//                    continue;
//                }
//                String repo_id = protectJson.get("repo_id").toString();
//                String repo_name = protectJson.get("name").toString();
//                try{
//                    repoPath=restInterfaceManager.getRepoPath(repo_id,"");
//                    if(repoPath!=null){
//                        commitCount = gitUtil.getCommitCount(repoPath,since,until,developer_name);
//                        if(commitCount != 0){
//                            String active = getActivityByRepoId(repo_id);
//                            result.put(repo_name,active);
//                        }
//                    }
//                }finally {
//                    if(repoPath!=null) {
//                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
//                    }
//                }
//            }
//        }
//
//        return result;
        return null;
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
                log.error("request project api failed  ---> {}",project_name);
                throw new RuntimeException("request project api failed  ---> "+ project_name);
            }
            JSONArray projects = result.getJSONArray("data");
            if(projects.size() == 0){
                log.info("do not have this project --> {}",project_name );
                throw new RuntimeException("do not have this project --> "+project_name);
            }
            if (projects.size() > 1){
                // 这一段的方法待优化 可以在多个project中取最近counts 次数的commits的度量
                log.info("more than one project named --> {}",project_name );
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
                log.error("can not find project by repo_id --->{}",repoMeasure.getRepo_id());
            }else {
                projectName = projects.getJSONObject(0).getString("name");
            }
            codeQuality.setCommitId(repoMeasure.getCommit_id());
            codeQuality.setProjectName(projectName);
            if(newIssueCounts == -1){
                log.info("this commit --> {} can not be compiled",repoMeasure.getCommit_id());
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

    @Override
    public Object getStatementByCondition(String repoId, String developer, String beginDate, String endDate) {
        if ("".equals(beginDate) || beginDate == null){
            beginDate = repoMeasureMapper.getFirstCommitDateByCondition(repoId,developer);
        }
        if ("".equals(endDate) || endDate == null){
            LocalDate today = LocalDate.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            endDate = df.format(today);
//            endDate = repoMeasureMapper.getLastCommitDateOfOneRepo(repoId,developer);
        }
        List<String> repoList = new ArrayList<>();
        if ("".equals(repoId) || repoId == null){
            repoList = repoMeasureMapper.getRepoListByDeveloper(developer,null,null);
        }else {
            repoList.add(repoId);
        }
        // 遍历所有repo，进行度量统计
        int developerAddStatement = 0;
        int developerDelStatement = 0;
        for (String repo : repoList){
            //获取代码新增、删除逻辑行数数据
            JSONObject statements = restInterfaceManager.getStatementsByCondition(repo, beginDate, endDate, developer);
            if  (statements != null){
                for(String str:statements.keySet()){
                    if (str.equals(developer)){
                        developerAddStatement += statements.getJSONObject(str).getIntValue("ADD");
                        developerDelStatement += statements.getJSONObject(str).getIntValue("DELETE");
                    }
                }
            }
        }
        int totalStatement = developerAddStatement + developerDelStatement;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sinceDay = LocalDate.parse(beginDate, fmt);
        LocalDate untilDay = LocalDate.parse(endDate, fmt);
        int totalDays = (int) (untilDay.toEpochDay()-sinceDay.toEpochDay());
        int workDays =  totalDays*5/7;
        double dayAvgStatement = totalStatement*1.0/workDays;

        Map<String,Object> map = new HashMap<>();
        map.put("totalStatement",totalStatement);
        map.put("workDays",workDays);
        map.put("dayAvgStatement",dayAvgStatement);
        return map;
    }

    @Override
    @Cacheable(cacheNames = {"developerMetrics"})
    public DeveloperMetrics getPortrait(String repoId, String developer, String beginDate, String endDate, String token, String tool) throws ParseException {
        if ("".equals(beginDate) || beginDate == null){
            beginDate = repoMeasureMapper.getFirstCommitDateByCondition(repoId,null);
        }
        if ("".equals(endDate) || endDate == null){
            LocalDate today = LocalDate.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            endDate = df.format(today);
        }
        List<Map<String, Object>> developerList = repoMeasureMapper.getDeveloperListByRepoId(repoId);
        int developerNumber = developerList.size();
        JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
        String branch = projects.getJSONObject(0).getString("branch");
        String repoName = projects.getJSONObject(0).getString("name");

        //获取程序员在本项目中第一次提交commit的日期
        LocalDateTime firstCommitDateTime;
        LocalDate firstCommitDate = null;
        JSONObject firstCommitDateData = restInterfaceManager.getFirstCommitDate(developer);
        JSONArray repoDateList = firstCommitDateData.getJSONArray("repos");
        for(int i=0;i<repoDateList.size();i++) {
            if (repoDateList.getJSONObject(i).get("repo_id").equals(repoId)){
                String dateString = repoDateList.getJSONObject(i).getString("first_commit_time");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                firstCommitDateTime = LocalDateTime.parse(dateString, fmt);
                firstCommitDateTime = firstCommitDateTime.plusHours(8);
                firstCommitDate = firstCommitDateTime.toLocalDate();
                break;
            }
        }

        //----------------------------------开发效率相关指标-------------------------------------
        Efficiency efficiency = getDeveloperEfficiency(repoId, beginDate, endDate, developer, branch, developerNumber);
        int totalLOC = efficiency.getTotalLOC();
        int developerAddStatement = efficiency.getDeveloperAddStatement();
        int totalAddStatement = efficiency.getTotalAddStatement();
        int developerValidLine = efficiency.getDeveloperValidLine();
        int totalValidLine = efficiency.getTotalValidLine();
        int developerLOC = efficiency.getDeveloperLOC();
        int developerCommitCount = efficiency.getDeveloperCommitCount();

        //----------------------------------代码质量相关指标-------------------------------------
        Quality quality = getDeveloperQuality(repoId,developer,beginDate,endDate,tool,token,developerNumber,totalLOC);

        //----------------------------------开发能力相关指标-------------------------------------
        Competence competence = getDeveloperCompetence(repoId,beginDate,endDate,developer,developerNumber,developerAddStatement,totalAddStatement,developerValidLine,totalValidLine);

        return new DeveloperMetrics(firstCommitDate, developerLOC, developerCommitCount, repoName, repoId, developer, efficiency, quality, competence);
    }

    private Efficiency getDeveloperEfficiency(String repoId, String beginDate, String endDate, String developer, String branch,
                                              int developerNumber){
        //提交频率指标
        int totalCommitCount = getCommitCountsByDuration(repoId, beginDate, endDate);
        int developerCommitCount = repoMeasureMapper.getCommitCountsByDuration(repoId, beginDate, endDate, developer);
        //代码量指标
        int developerLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, developer);
        int totalLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, "");
        //获取代码新增、删除逻辑行数数据
        JSONObject statements = restInterfaceManager.getStatements(repoId, beginDate, endDate, branch);
        int developerAddStatement = 0;
        int totalAddStatement = 0;
        int developerDelStatement = 0;
        int totalDelStatement = 0;
        if  (statements != null){
            for(String str:statements.keySet()){
                if (str.equals("total")){
                    continue;
                }
                if (str.equals(developer)){
                    developerAddStatement = statements.getJSONObject(str).getIntValue("ADD");
                    developerDelStatement = statements.getJSONObject(str).getIntValue("DELETE");
                }
                totalAddStatement += statements.getJSONObject(str).getIntValue("ADD");
                totalDelStatement += statements.getJSONObject(str).getIntValue("DELETE");
            }
        }
        JSONObject validLines = restInterfaceManager.getValidLine(repoId, beginDate, endDate, branch);
        int developerValidLine = 0;
        int totalValidLine = 0;
        if (validLines != null){
            for(String key:validLines.keySet()){
                if (key.equals("total")){
                    continue;
                }
                if (key.equals(developer)){
                    developerValidLine = validLines.getIntValue(key);
                }
                totalValidLine += validLines.getIntValue(key);
            }
        }
        return Efficiency.builder()
                .developerNumber(developerNumber)
                .totalCommitCount(totalCommitCount)
                .developerCommitCount(developerCommitCount)
                .totalLOC(totalLOC)
                .developerLOC(developerLOC)
                .developerAddStatement(developerAddStatement)
                .totalAddStatement(totalAddStatement)
                .developerDelStatement(developerDelStatement)
                .totalDelStatement(totalDelStatement)
                .developerValidLine(developerValidLine)
                .totalValidLine(totalValidLine)
                .build();
    }

    private Quality getDeveloperQuality(String repoId, String developer, String beginDate, String endDate, String tool, String token, int developerNumber, int totalLOC){
        //个人规范类issue数
        int developerStandardIssueCount = restInterfaceManager.getIssueCountByConditions(developer, repoId, beginDate, endDate, tool, "standard", token);
        //个人安全类issue数
        int developerSecurityIssueCount = restInterfaceManager.getIssueCountByConditions(developer, repoId, beginDate, endDate, tool, "security", token);
        //repo总issue数
        int totalIssueCount = restInterfaceManager.getIssueCountByConditions("", repoId, beginDate, endDate, tool, "", token);
        JSONArray issueList = restInterfaceManager.getNewElmIssueCount(repoId, beginDate, endDate, tool, token);
        int developerNewIssueCount = 0;//个人新增缺陷数
        int totalNewIssueCount = 0;//总新增缺陷数
        if (!issueList.isEmpty()){
            for (int i = 0; i < issueList.size(); i++){
                JSONObject each = issueList.getJSONObject(i);
                String developerName = each.getString("developer");
                int newIssueCount = each.getIntValue("newIssueCount");
                if (developer.equals(developerName)){
                    developerNewIssueCount = newIssueCount;
                }
                totalNewIssueCount += newIssueCount;
            }
        }
        return Quality.builder()
                .developerNumber(developerNumber)
                .developerNewIssueCount(developerNewIssueCount)
                .developerSecurityIssueCount(developerSecurityIssueCount)
                .developerStandardIssueCount(developerStandardIssueCount)
                .totalIssueCount(totalIssueCount)
                .totalNewIssueCount(totalNewIssueCount)
                .totalLOC(totalLOC)
                .build();
    }

    private Competence getDeveloperCompetence(String repoId, String beginDate, String endDate, String developer,
                                              int developerNumber, int developerAddStatement, int totalAddStatement,
                                              int developerValidLine, int totalValidLine){
        int developerAddLine = repoMeasureMapper.getAddLinesByDuration(repoId, beginDate, endDate, developer);
        JSONObject cloneMeasure = restInterfaceManager.getCloneMeasure(repoId, developer, beginDate, endDate);
        int increasedCloneLines = 0;
        int selfIncreasedCloneLines = 0;
        int eliminateCloneLines = 0;
        int allEliminateCloneLines = 0;
        if (cloneMeasure != null){
            increasedCloneLines = Integer.parseInt(cloneMeasure.getString("increasedCloneLines"));
            selfIncreasedCloneLines = Integer.parseInt(cloneMeasure.getString("selfIncreasedCloneLines"));
            eliminateCloneLines = Integer.parseInt(cloneMeasure.getString("eliminateCloneLines"));
            allEliminateCloneLines = Integer.parseInt(cloneMeasure.getString("allEliminateCloneLines"));
        }
        JSONObject focusMeasure = restInterfaceManager.getFocusFilesCount(repoId, beginDate, endDate);
        int totalChangedFile = 0;
        int developerFocusFile = 0;
        if (focusMeasure != null){
            totalChangedFile = focusMeasure.getIntValue("total");
            developerFocusFile = focusMeasure.getJSONObject("developer").getIntValue(developer);
        }

        JSONObject changedCodeInfo = restInterfaceManager.getChangedCodeAge(repoId, beginDate, endDate, developer);
        double changedCodeAVGAge = 0;
        int changedCodeMAXAge = 0;
        if (changedCodeInfo != null){
            changedCodeAVGAge = changedCodeInfo.getDoubleValue("average");
            changedCodeMAXAge = changedCodeInfo.getIntValue("max");
        }

        JSONObject deletedCodeInfo = restInterfaceManager.getDeletedCodeAge(repoId, beginDate, endDate, developer);
        double deletedCodeAVGAge = 0;
        int deletedCodeMAXAge = 0;
        if (deletedCodeInfo != null){
            deletedCodeAVGAge = deletedCodeInfo.getDoubleValue("average");
            deletedCodeMAXAge = deletedCodeInfo.getIntValue("max");
        }
        int repoAge = restInterfaceManager.getRepoAge(repoId, endDate);

        return Competence.builder()
                .developerNumber(developerNumber)
                .developerAddStatement(developerAddStatement)
                .totalAddStatement(totalAddStatement)
                .developerAddLine(developerAddLine)
                .increasedCloneLines(increasedCloneLines)
                .selfIncreasedCloneLines(selfIncreasedCloneLines)
                .eliminateCloneLines(eliminateCloneLines)
                .allEliminateCloneLines(allEliminateCloneLines)
                .totalChangedFile(totalChangedFile)
                .developerFocusFile(developerFocusFile)
                .changedCodeAVGAge(changedCodeAVGAge)
                .changedCodeMAXAge(changedCodeMAXAge)
                .deletedCodeAVGAge(deletedCodeAVGAge)
                .deletedCodeMAXAge(deletedCodeMAXAge)
                .repoAge(repoAge)
                .developerValidLine(developerValidLine)
                .totalValidLine(totalValidLine)
                .build();
    }


    @CacheEvict(cacheNames = {"portraitLevel","developerMetrics"})
    public void reloadCache() {
    }

    @Cacheable(cacheNames = {"portraitLevel"})
    @Override
    public Object getPortraitLevel(String developer, String since, String until, String token) throws ParseException {
        //获取developerMetricsList
        List<String> repoList = repoMeasureMapper.getRepoListByDeveloper(developer,since,until);
        if (repoList.size()==0){
            return "选定时间段内无参与项目！";
        }
        List<DeveloperMetrics> developerMetricsList = new ArrayList<>();
        if (StringUtils.isEmptyOrNull(until)){
            LocalDate today = LocalDate.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            until = df.format(today);
        }
        for (String repoId : repoList) {
            JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
            for (int i = 0; i < projects.size(); i++){
                String tool = projects.getJSONObject(i).getString("type");
                String repoName = projects.getJSONObject(i).getString("name");
                log.info("Current repo is : " + repoName + ", the issue_scan_type is " + tool);
                //只添加被sonarqube扫描过的项目，findbugs之后会逐渐被废弃
                if ("sonarqube".equals(tool)){
                    if (StringUtils.isEmptyOrNull(since)){
                        since = repoMeasureMapper.getFirstCommitDateByCondition(repoId,null);
                    }
//                    String endDate = repoMeasureMapper.getLastCommitDateOfOneRepo(repoId);
                    log.info("Start to get portrait of " + developer + " in repo : " + repoName);
                    DeveloperMetrics metrics = getPortrait(repoId, developer, since, until, token, tool);
                    developerMetricsList.add(metrics);
                    log.info("Successfully get portrait of " + developer + " in repo : " + repoName);
                }
            }
        }
        log.info("Get portrait of " + developer + " complete!" );
        //获取第一次提交commit的日期
        LocalDateTime firstCommitDateTime;
        LocalDate firstCommitDate = null;
        JSONObject firstCommitDateData = restInterfaceManager.getFirstCommitDate(developer);
        JSONObject repoDateList = firstCommitDateData.getJSONObject("repos_summary");
        String dateString = repoDateList.getString("first_commit_time_summary");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        firstCommitDateTime = LocalDateTime.parse(dateString, fmt);
        firstCommitDateTime = firstCommitDateTime.plusHours(8);
        firstCommitDate = firstCommitDateTime.toLocalDate();

        int totalCommitCount = repoMeasureMapper.getCommitCountsByDuration(null, null, null, developer);
        int totalLOC = repoMeasureMapper.getLOCByCondition(null,developer,null,null);
        List<Map<String, Object>> commitDays = repoMeasureMapper.getCommitDays(null,developer,null,null);
        int days = commitDays.size();
        int dayAverageLOC = totalLOC/days;
        //todo 日后需要添加程序员类型接口 目前统一认为是java后端工程师
        String developerType = "Java后端工程师";
        return new DeveloperPortrait(firstCommitDate,totalLOC,dayAverageLOC,totalCommitCount,developer,developerType,developerMetricsList);
    }


    @Cacheable(cacheNames = {"developerMetricsNew"})
    public cn.edu.fudan.measureservice.portrait2.DeveloperMetrics getDeveloperMetrics(String repoId, String developer, String beginDate, String endDate, String token, String tool) {
        if ("".equals(beginDate) || beginDate == null){
            beginDate = repoMeasureMapper.getFirstCommitDateByCondition(repoId,null);
        }
        if ("".equals(endDate) || endDate == null){
            LocalDate today = LocalDate.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            endDate = df.format(today);
        }
//        List<Map<String, Object>> developerList = repoMeasureMapper.getDeveloperListByRepoId(repoId);
//        int developerNumber = developerList.size();
        JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
        String branch = projects.getJSONObject(0).getString("branch");
        String repoName = projects.getJSONObject(0).getString("name");

        //获取程序员在本项目中第一次提交commit的日期
        LocalDateTime firstCommitDateTime;
        LocalDate firstCommitDate = null;
        JSONObject firstCommitDateData = restInterfaceManager.getFirstCommitDate(developer);
        JSONArray repoDateList = firstCommitDateData.getJSONArray("repos");
        for(int i=0;i<repoDateList.size();i++) {
            if (repoDateList.getJSONObject(i).get("repo_id").equals(repoId)){
                String dateString = repoDateList.getJSONObject(i).getString("first_commit_time");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                firstCommitDateTime = LocalDateTime.parse(dateString, fmt);
                firstCommitDateTime = firstCommitDateTime.plusHours(8);
                firstCommitDate = firstCommitDateTime.toLocalDate();
                break;
            }
        }

        int developerLOC = repoMeasureMapper.getLOCByCondition(repoId,developer,beginDate,endDate);
        int developerCommitCount = repoMeasureMapper.getCommitCountsByDuration(repoId, beginDate, endDate, developer);
        //获取代码新增、删除逻辑行数数据
        JSONObject statements = restInterfaceManager.getStatements(repoId, beginDate, endDate, branch);
        int developerAddStatement = 0;
        int totalAddStatement = 0;
        int developerDelStatement = 0;
        int totalDelStatement = 0;
        if  (statements != null){
            for(String str:statements.keySet()){
                if (str.equals("total")){
                    continue;
                }
                if (str.equals(developer)){
                    developerAddStatement = statements.getJSONObject(str).getIntValue("ADD");
                    developerDelStatement = statements.getJSONObject(str).getIntValue("DELETE");
                }
                totalAddStatement += statements.getJSONObject(str).getIntValue("ADD");
                totalDelStatement += statements.getJSONObject(str).getIntValue("DELETE");
            }
        }

        //开发效率相关指标
        cn.edu.fudan.measureservice.portrait2.Efficiency efficiency = getEfficiency(repoId, beginDate, endDate, developer, tool, token);

        //代码质量相关指标
        cn.edu.fudan.measureservice.portrait2.Quality quality = getQuality(repoId,developer,beginDate,endDate,tool,token,developerLOC,developerCommitCount);

        //贡献价值相关指标
        cn.edu.fudan.measureservice.portrait2.Contribution contribution = getContribution(repoId,beginDate,endDate,developer,developerLOC,branch,developerAddStatement,totalAddStatement);

        return new cn.edu.fudan.measureservice.portrait2.DeveloperMetrics(firstCommitDate, developerAddStatement+developerDelStatement, developerCommitCount, repoName, branch, developer, efficiency, quality, contribution);
    }

    private cn.edu.fudan.measureservice.portrait2.Efficiency getEfficiency(String repoId,String beginDate, String endDate, String developer, String tool, String token){

        int jiraBug = 0;
        int jiraFeature = 0;
        int solvedSonarIssue = 0;
        int days = 0;
        JSONArray jiraResponse = restInterfaceManager.getJiraMsgOfOneDeveloper(developer, repoId);
        if (jiraResponse != null){
            JSONObject jiraBugData = jiraResponse.getJSONObject(4);
            JSONObject jiraFeatureData = jiraResponse.getJSONObject(5);
            jiraBug = jiraBugData.getIntValue("completedBugNum");
            jiraFeature = jiraFeatureData.getIntValue("completedFeatureNum");
        }

        JSONObject sonarResponse = restInterfaceManager.getDayAvgSolvedIssue(developer,repoId,beginDate,endDate,tool,token);
        if (sonarResponse != null){
            solvedSonarIssue = sonarResponse.getIntValue("solvedIssuesCount");
            days = (int) sonarResponse.getDoubleValue("days");
        }
        return cn.edu.fudan.measureservice.portrait2.Efficiency.builder()
                .jiraBug(jiraBug)
                .jiraFeature(jiraFeature)
                .solvedSonarIssue(solvedSonarIssue)
                .days(days)
                .build();
    }
    private cn.edu.fudan.measureservice.portrait2.Quality getQuality(String repoId, String developer, String beginDate, String endDate, String tool, String token, int developerLOC, int developerCommitCount){
        //个人规范类issue数
        int developerStandardIssueCount = restInterfaceManager.getIssueCountByConditions(developer, repoId, beginDate, endDate, tool, "standard", token);
        //repo总issue数
        int totalIssueCount = restInterfaceManager.getIssueCountByConditions("", repoId, beginDate, endDate, tool, "", token);
        JSONArray issueList = restInterfaceManager.getNewElmIssueCount(repoId, beginDate, endDate, tool, token);
        int developerNewIssueCount = 0;//个人新增缺陷数
        int totalNewIssueCount = 0;//总新增缺陷数
        if (!issueList.isEmpty()){
            for (int i = 0; i < issueList.size(); i++){
                JSONObject each = issueList.getJSONObject(i);
                String developerName = each.getString("developer");
                int newIssueCount = each.getIntValue("newIssueCount");
                if (developer.equals(developerName)){
                    developerNewIssueCount = newIssueCount;
                }
                totalNewIssueCount += newIssueCount;
            }
        }

        int developerJiraCount = 0;
        List<Map<String, Object>> commitMsgList = repoMeasureMapper.getCommitMsgByCondition(repoId, developer, beginDate, endDate);
        for (Map<String, Object> map : commitMsgList) {
            //以下操作是为了获取jira信息
            String commitMessage = map.get("commit_message").toString();
            String jiraID = getJiraIDFromCommitMsg(commitMessage);
            if (!"noJiraID".equals(jiraID)){
                developerJiraCount++;
            }
        }

        int developerJiraBugCount = 0;
        int totalJiraBugCount = 0;
        JSONObject jiraBugData = restInterfaceManager.getDefectRate(developer,repoId,beginDate,endDate);
        if (jiraBugData!=null){
            developerJiraBugCount = jiraBugData.getIntValue("individual_bugs");
            totalJiraBugCount = jiraBugData.getIntValue("team_bugs");
        }
        return cn.edu.fudan.measureservice.portrait2.Quality.builder()
                .developerStandardIssueCount(developerStandardIssueCount)
                .totalIssueCount(totalIssueCount)
                .developerNewIssueCount(developerNewIssueCount)
                .totalNewIssueCount(totalNewIssueCount)
                .developerLOC(developerLOC)
                .developerCommitCount(developerCommitCount)
                .developerJiraCount(developerJiraCount)
                .developerJiraBugCount(developerJiraBugCount)
                .totalJiraBugCount(totalJiraBugCount)
                .build();
    }

    private cn.edu.fudan.measureservice.portrait2.Contribution getContribution(String repoId, String beginDate, String endDate, String developer,
                                                                               int developerLOC, String branch, int developerAddStatement, int totalAddStatement){
        int totalLOC = repoMeasureMapper.getLOCByCondition(repoId,null,beginDate,endDate);
        int developerAddLine = repoMeasureMapper.getAddLinesByDuration(repoId, beginDate, endDate, developer);

        JSONObject validLines = restInterfaceManager.getValidLine(repoId, beginDate, endDate, branch);
        int developerValidLine = 0;
        int totalValidLine = 0;
        if (validLines != null){
            for(String key:validLines.keySet()){
                if (key.equals("total")){
                    continue;
                }
                if (key.equals(developer)){
                    developerValidLine = validLines.getIntValue(key);
                }
                totalValidLine += validLines.getIntValue(key);
            }
        }
        JSONObject cloneMeasure = restInterfaceManager.getCloneMeasure(repoId, developer, beginDate, endDate);
        int increasedCloneLines = 0;
        if (cloneMeasure != null){
            increasedCloneLines = Integer.parseInt(cloneMeasure.getString("increasedCloneLines"));
        }

        int developerAssignedJiraCount = 0;//个人被分配到的jira任务个数（注意不是次数）
        int totalAssignedJiraCount = 0;//团队被分配到的jira任务个数（注意不是次数）
        int developerSolvedJiraCount = 0;//个人解决的jira任务个数（注意不是次数）
        int totalSolvedJiraCount = 0;//团队解决的jira任务个数（注意不是次数）

        JSONObject assignedJiraData = restInterfaceManager.getAssignedJiraRate(developer,repoId,beginDate,endDate);
        if (assignedJiraData!=null){
            developerAssignedJiraCount = assignedJiraData.getIntValue("individual_assigned_jira_num");
            totalAssignedJiraCount = assignedJiraData.getIntValue("team_assigned_jira_num");
        }
        JSONObject solvedAssignedJiraData = restInterfaceManager.getSolvedAssignedJiraRate(developer,repoId,beginDate,endDate);
        if (solvedAssignedJiraData!=null){
            developerSolvedJiraCount = solvedAssignedJiraData.getIntValue("individual_solved_assigned_jira_num");
            totalSolvedJiraCount = solvedAssignedJiraData.getIntValue("team_solved_assigned_jira_num");
        }
        return Contribution.builder()
                .developerLOC(developerLOC)
                .totalLOC(totalLOC)
                .developerAddStatement(developerAddStatement)
                .totalAddStatement(totalAddStatement)
                .developerAddLine(developerAddLine)
                .developerValidLine(developerValidLine)
                .totalValidLine(totalValidLine)
                .increasedCloneLines(increasedCloneLines)
                .developerAssignedJiraCount(developerAssignedJiraCount)
                .totalAssignedJiraCount(totalAssignedJiraCount)
                .developerSolvedJiraCount(developerSolvedJiraCount)
                .totalSolvedJiraCount(totalSolvedJiraCount)
                .build();
    }


    @Cacheable(cacheNames = {"portraitCompetence"})
    @Override
    public Object getPortraitCompetence(String developer,String since,String until, String token) throws ParseException {
        //获取developerMetricsList
        List<String> repoList = repoMeasureMapper.getRepoListByDeveloper(developer,since,until);
        if (repoList.size()==0){
            return "选定时间段内无参与项目！";
        }
        List<cn.edu.fudan.measureservice.portrait2.DeveloperMetrics> developerMetricsList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        if (StringUtils.isEmptyOrNull(until)){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            until = df.format(today);
        }
        for (String repoId : repoList) {
            JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
            for (int i = 0; i < projects.size(); i++){
                String tool = projects.getJSONObject(i).getString("type");
                String repoName = projects.getJSONObject(i).getString("name");
                log.info("Current repo is : " + repoName + ", the issue_scan_type is " + tool);
                //只添加被sonarqube扫描过的项目，findbugs之后会逐渐被废弃
                if ("sonarqube".equals(tool)){
                    if (StringUtils.isEmptyOrNull(since)){
                        since = repoMeasureMapper.getFirstCommitDateByCondition(repoId,null);
                    }
                    //String endDate = repoMeasureMapper.getLastCommitDateOfOneRepo(repoId);
                    log.info("Start to get portrait of " + developer + " in repo : " + repoName);
                    cn.edu.fudan.measureservice.portrait2.DeveloperMetrics metrics = getDeveloperMetrics(repoId, developer, since, until, token, tool);
                    developerMetricsList.add(metrics);
                    log.info("Successfully get portrait of " + developer + " in repo : " + repoName);
                }
            }
        }
        log.info("Get portrait of " + developer + " complete!" );
        //获取第一次提交commit的日期
        LocalDateTime firstCommitDateTime;
        LocalDate firstCommitDate = null;
        JSONObject firstCommitDateData = restInterfaceManager.getFirstCommitDate(developer);
        JSONObject repoDateList = firstCommitDateData.getJSONObject("repos_summary");
        String dateString = repoDateList.getString("first_commit_time_summary");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        firstCommitDateTime = LocalDateTime.parse(dateString, fmt);
        firstCommitDateTime = firstCommitDateTime.plusHours(8);
        firstCommitDate = firstCommitDateTime.toLocalDate();


        int totalCommitCount = repoMeasureMapper.getCommitCountsByDuration(null, null, null, developer);

        //todo 日后需要添加程序员类型接口 目前统一认为是java后端工程师
        String developerType = "Java后端工程师";

        // 获取开发者在所有项目中的整个的用户画像
        cn.edu.fudan.measureservice.portrait2.DeveloperMetrics totalDeveloperMetrics = getTotalDeveloperMetrics(developerMetricsList,developer,firstCommitDate);

        int totalStatement = totalDeveloperMetrics.getTotalStatement();
        int totalDays = (int) (today.toEpochDay()-firstCommitDate.toEpochDay());
        int workDays =  totalDays*5/7;
        int dayAverageStatement = totalStatement/workDays;
        return new cn.edu.fudan.measureservice.portrait2.DeveloperPortrait(firstCommitDate,totalStatement,dayAverageStatement,totalCommitCount,developer,developerType,developerMetricsList,totalDeveloperMetrics);
    }

    /**
     *
     * @param developerMetricsList 每个项目的画像数据
     * @param developer 开发者
     * @return 返回开发者在所有项目当中的整体画像数据
     */
    private cn.edu.fudan.measureservice.portrait2.DeveloperMetrics getTotalDeveloperMetrics(List<cn.edu.fudan.measureservice.portrait2.DeveloperMetrics> developerMetricsList, String developer, LocalDate firstCommitDate){
        int totalStatement = 0;
        int totalCommitCount = 0;

        //efficiency
        int totalDays = (int) (LocalDate.now().toEpochDay()-firstCommitDate.toEpochDay());
        int workDays =  totalDays*5/7;
        int jiraBug = 0;
        int jiraFeature = 0;
        int solvedSonarIssue = 0;

        //quality
        int developerStandardIssueCount = 0;
        int totalIssueCount = 0;//团队总问题数
        int developerNewIssueCount = 0;//个人引入问题
        int totalNewIssueCount = 0;//团队引入问题
        int developerLOC = 0;//个人addLines+delLines
        int developerCommitCount = 0;//个人提交的commit总数
        int developerJiraCount = 0;//个人提交的commit当中 关联有jira的个数
        int developerJiraBugCount = 0;//个人jira任务中属于bug类型的数量
        int totalJiraBugCount = 0;//团队jira任务中属于bug类型的数量

        //contribution
        //int developerLOC
        int totalLOC = 0;
        int developerAddStatement = 0;
        int totalAddStatement = 0;
        int developerAddLine = 0;
        int developerValidLine = 0;//个人存活语句
        int totalValidLine = 0;//团队存活语句
        int increasedCloneLines = 0;//个人新增重复代码行数
        int developerAssignedJiraCount = 0;//个人被分配到的jira任务个数（注意不是次数）
        int totalAssignedJiraCount = 0;//团队被分配到的jira任务个数（注意不是次数）
        int developerSolvedJiraCount = 0;//个人解决的jira任务个数（注意不是次数）
        int totalSolvedJiraCount = 0;//团队解决的jira任务个数（注意不是次数）

        //对每个项目的数据进行累加，便于求整体的画像数据
        for (int i = 0; i < developerMetricsList.size(); i++){
            cn.edu.fudan.measureservice.portrait2.DeveloperMetrics metric = developerMetricsList.get(i);
            cn.edu.fudan.measureservice.portrait2.Efficiency efficiency = metric.getEfficiency();
            cn.edu.fudan.measureservice.portrait2.Quality quality = metric.getQuality();
            Contribution contribution = metric.getContribution();

            totalStatement += metric.getTotalStatement();
            totalCommitCount += metric.getTotalCommitCount();

            jiraBug += efficiency.getJiraBug();
            jiraFeature += efficiency.getJiraFeature();
            solvedSonarIssue += efficiency.getSolvedSonarIssue();

            developerStandardIssueCount += quality.getDeveloperStandardIssueCount();
            totalIssueCount += quality.getTotalIssueCount();
            developerNewIssueCount += quality.getDeveloperNewIssueCount();
            totalNewIssueCount += quality.getTotalNewIssueCount();
            developerLOC += quality.getDeveloperLOC();
            developerCommitCount += quality.getDeveloperCommitCount();
            developerJiraCount += quality.getDeveloperJiraCount();
            developerJiraBugCount += quality.getDeveloperJiraBugCount();
            totalJiraBugCount += quality.getTotalJiraBugCount();

            totalLOC += contribution.getTotalLOC();
            developerAddStatement += contribution.getDeveloperAddStatement();
            totalAddStatement += contribution.getTotalAddStatement();
            developerAddLine += contribution.getDeveloperAddLine();
            developerValidLine += contribution.getDeveloperValidLine();
            totalValidLine += contribution.getTotalValidLine();
            increasedCloneLines += contribution.getIncreasedCloneLines();
            developerAssignedJiraCount += contribution.getDeveloperAssignedJiraCount();
            totalAssignedJiraCount += contribution.getTotalAssignedJiraCount();
            developerSolvedJiraCount += contribution.getDeveloperSolvedJiraCount();
            totalSolvedJiraCount += contribution.getTotalSolvedJiraCount();
        }
        cn.edu.fudan.measureservice.portrait2.Efficiency totalEfficiency = cn.edu.fudan.measureservice.portrait2.Efficiency.builder()
                .jiraBug(jiraBug).jiraFeature(jiraFeature).solvedSonarIssue(solvedSonarIssue).days(workDays).build();

        cn.edu.fudan.measureservice.portrait2.Quality totalQuality = cn.edu.fudan.measureservice.portrait2.Quality.builder()
                .developerStandardIssueCount(developerStandardIssueCount).totalIssueCount(totalIssueCount)
                .developerNewIssueCount(developerNewIssueCount).totalNewIssueCount(totalNewIssueCount)
                .developerLOC(developerLOC).developerCommitCount(developerCommitCount).developerJiraCount(developerJiraCount)
                .developerJiraBugCount(developerJiraBugCount).totalJiraBugCount(totalJiraBugCount).build();

        Contribution totalContribution = Contribution.builder().developerLOC(developerLOC).totalLOC(totalLOC)
                .developerAddStatement(developerAddStatement).totalAddStatement(totalAddStatement)
                .developerAddLine(developerAddLine).developerValidLine(developerValidLine).totalValidLine(totalValidLine)
                .increasedCloneLines(increasedCloneLines).developerAssignedJiraCount(developerAssignedJiraCount)
                .totalAssignedJiraCount(totalAssignedJiraCount).developerSolvedJiraCount(developerSolvedJiraCount)
                .totalSolvedJiraCount(totalSolvedJiraCount).build();

        return new cn.edu.fudan.measureservice.portrait2.DeveloperMetrics(totalStatement, totalCommitCount, totalEfficiency,totalQuality,totalContribution);

    }


    @Override
    public Object getCommitMsgByCondition(String repoId, String developer, String beginDate, String endDate) {
        List<Map<String, Object>> commitMsgList = repoMeasureMapper.getCommitMsgByCondition(repoId, developer, beginDate, endDate);
        for (Map<String, Object> map : commitMsgList) {
            //将数据库中timeStamp/dateTime类型转换成指定格式的字符串 map.get("commit_time") 这个就是数据库中dateTime类型
            String commit_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(map.get("commit_time"));
            map.put("commit_time", commit_time);
        }
        return commitMsgList;
    }

    @Override
    public Object getJiraMeasureInfo(String repoId, String developer, String beginDate, String endDate) {
        if ("".equals(beginDate) || beginDate == null){
            beginDate = repoMeasureMapper.getFirstCommitDateByCondition(repoId,developer);
        }
        if ("".equals(endDate) || endDate == null){
//            LocalDate today = LocalDate.now();
//            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            endDate = df.format(today);
            endDate = repoMeasureMapper.getLastCommitDateOfOneRepo(repoId,developer);
        }
        List<String> repoList = new ArrayList<>();
        if ("".equals(repoId) || repoId == null){
            repoList = repoMeasureMapper.getRepoListByDeveloper(developer,null,null);
        }else {
            repoList.add(repoId);
        }

        // 遍历所有repo，进行jira度量统计
        for (String repo : repoList){
            // 0. 获取开发者完成的jira任务列表 即jira的ID列表

            // 1. 获取完成jira任务需要的commit数量

            // 2. 获取这些commit的工作日天数

        }


        return null;
    }

    @Cacheable(cacheNames = {"developerRecentNews"})
    @Override
    public Object getDeveloperRecentNews(String repoId, String developer, String beginDate, String endDate) {
        List<Map<String, Object>> commitMsgList = repoMeasureMapper.getCommitMsgByCondition(repoId, developer, beginDate, endDate);
        for (Map<String, Object> map : commitMsgList) {
            //将数据库中timeStamp/dateTime类型转换成指定格式的字符串 map.get("commit_time") 这个就是数据库中dateTime类型
            String commit_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(map.get("commit_time"));
            map.put("commit_time", commit_time);
            //以下操作是为了获取jira信息
            String commitMessage = map.get("commit_message").toString();
            String jiraID = getJiraIDFromCommitMsg(commitMessage);
            JSONArray jiraResponse = restInterfaceManager.getJiraInfoByKey("key",jiraID);
            if (jiraResponse == null || jiraResponse.isEmpty()){
                map.put("jira_info", "本次commit不含jira单号");
            }else {
                map.put("jira_info", jiraResponse.get(0));
            }
        }
        return commitMsgList;
    }

    /**
     * 根据commit message 返回 对应的 jira 单号
     */
    private String getJiraIDFromCommitMsg(String commitMsg){
        // 使用Pattern类的compile方法，传入jira单号的正则表达式，得到一个Pattern对象
        Pattern pattern = Pattern.compile("[A-Z][A-Z0-9]*-[0-9]+");
        // 调用pattern对象的matcher方法，传入需要匹配的字符串， 得到一个匹配器对象
        Matcher matcher = pattern.matcher(commitMsg);

        // 从字符串开头，返回匹配到的第一个字符串
        if (matcher.find()) {
            // 输出第一次匹配的内容
            log.info("jira ID is : {}",matcher.group());
            return matcher.group();
        }
        return "noJiraID" ;
    }

    @Override
    public Object getDeveloperList(String repoId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> developerList = repoMeasureMapper.getDeveloperListByRepoId(repoId);
        for (int i = 0; i < developerList.size(); i++){
            Map<String,Object> map = developerList.get(i);
            String developerName = map.get("developer_name").toString();
            int involvedRepoCount = (int) getDeveloperInvolvedRepoCount(developerName);
            map.put("involvedRepoCount",involvedRepoCount);
            result.add(map);
        }
        return result;
    }

    @Override
    public Object getDeveloperInvolvedRepoCount(String developer) {
        List<String> repoList = repoMeasureMapper.getRepoListByDeveloper(developer,null,null);
        return repoList.size();
    }
}
