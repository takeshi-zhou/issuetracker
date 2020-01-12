package cn.edu.fudan.issueservice.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import	java.util.stream.Collectors;/**
 * @description:
 * @author: fancying
 * @create: 2019-04-02 15:27
 **/

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.component.RestInterfaceManagerUtil;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.dao.ScanResultDao;
import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.domain.statistics.CodeQualityResponse;
import cn.edu.fudan.issueservice.domain.statistics.DeveloperQuality;
import cn.edu.fudan.issueservice.domain.statistics.Quality;
import cn.edu.fudan.issueservice.domain.statistics.TimeQuality;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import cn.edu.fudan.issueservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class IssueMeasureInfoServiceImpl implements IssueMeasureInfoService {


    private static Logger logger = LoggerFactory.getLogger(IssueMeasureInfoServiceImpl.class);

    private RawIssueDao rawIssueDao;
    private IssueDao issueDao;
    private RestInterfaceManager restInterfaceManager;
    private ScanResultDao scanResultDao;
    private RestInterfaceManagerUtil restInterfaceManagerUtil;

    public IssueMeasureInfoServiceImpl(RawIssueDao rawIssueDao,
                                       IssueDao issueDao,
                                       RestInterfaceManager restInterfaceManager,
                                       ScanResultDao scanResultDao,
                                       RestInterfaceManagerUtil restInterfaceManagerUtil) {
        this.rawIssueDao = rawIssueDao;
        this.issueDao = issueDao;
        this.restInterfaceManager = restInterfaceManager;
        this.scanResultDao = scanResultDao;
        this.restInterfaceManagerUtil = restInterfaceManagerUtil;
    }

    @Override
    public int numberOfRemainingIssue(String repoId, String commit, String spaceType, String detail) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return rawIssueDao.getNumberOfRemainingIssue(repoId, commit);
        }
        if (SpaceType.PACKAGE.getLevel().equals(spaceType)) {

            // package name 需要做处理
            return rawIssueDao.getNumberOfRemainingIssueBasePackage(repoId, commit,
                    "%" + detail.replace('.','/') + "%");
        }
        if (SpaceType.FILE.getLevel().equals(spaceType)) {

            return rawIssueDao.getNumberOfRemainingIssueBaseFile(repoId, commit, detail);
        }
        // 需要单独引入用户问题记录表 ？

        return -1;
    }




    @Override
    public int numberOfNewIssue(String duration, String spaceType, String detail) {
        // duration: 2018.01.01-2018.12.12
        if (duration.length() < 21) {
            throw new RuntimeException("duration error!");
        }
        String start = duration.substring(0,10);
        String end = duration.substring(11,21);
        //List<String> commits = restInterfaceManager.getScanCommitsIdByDuration(detail, start, end);

/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return ;
        }*/

        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return issueDao.getNumberOfNewIssueByDuration(detail, start, end);
        }


        return -1;
    }

    @Override
    public int numberOfNewIssueByCommit(String repoId, String commitId, String spaceType,String category) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return scanResultDao.getNumberOfNewIssueByCommit(repoId, commitId,category);
        }
        return -1;
    }

    @Override
    public int numberOfEliminateIssue(String duration, String spaceType, String detail) {
        // duration: 2018.01.01-2018.12.12
        if (duration.length() < 21) {
            throw new RuntimeException("duration error!");
        }
        String start = duration.substring(0,10);
        String end = duration.substring(11,21);

        // detail 是repoId
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return issueDao.getNumberOfEliminateIssueByDuration(detail, start, end);
        }
/*        if (SpaceType.DEVELOPER.getLevel().equals(spaceType)) {
            return ;
        }*/

        return -1;
    }

    @Override
    public int numberOfEliminateIssueByCommit(String repoId, String commitId, String spaceType,String category) {
        // 项目某commit下的现有问题数
        if (SpaceType.PROJECT.getLevel().equals(spaceType)) {
            return scanResultDao.getNumberOfEliminateIssueByCommit(repoId, commitId,category);
        }
        return -1;
    }

    @Override
    public List<IssueCountPo> getIssueCountEachCommit(String repoId,String category, String since, String until) {
        return scanResultDao.getScanResultsEachCommit(repoId,category,since,until);
    }

    @Override
    public IssueCountMeasure getIssueCountMeasureByRepo(String repoId, String category, String since, String until) {
        IssueCountMeasure issueCountMeasure=new IssueCountMeasure();
        issueCountMeasure.setNewIssueCount(issueDao.getNumberOfNewIssueByDuration(repoId, since, until));
        issueCountMeasure.setEliminatedIssueCount(issueDao.getNumberOfEliminateIssueByDuration(repoId, since, until));
        return issueCountMeasure;
    }

    @Override
    public List<IssueCountDeveloper> getIssueCountMeasureByDeveloper(String repoId, String category, String since, String until) {
        return scanResultDao.getScanResultsEachDeveloper(repoId, category, since, until);
    }

    @Override
    public Object getNotSolvedIssueCountByCategoryAndRepoId(String repoId, String category,String commitId) {
        Map<String,Integer> issueCount = new HashMap<>();
        Map<String,Integer> result = new LinkedHashMap<>();

        if(commitId == null){
            List<Issue> issues = issueDao.getNotSolvedIssueAllListByCategoryAndRepoId(repoId,category);

            for (Issue issue:
                    issues) {
                String issueType = issue.getType();
                int count = issueCount.get(issueType) != null ? issueCount.get(issueType) : 0;
                issueCount.put(issueType,++count);
            }
        }else{
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId,category,commitId);
            for (RawIssue rawIssue:
                    rawIssues) {
                String rawIssueType = rawIssue.getType();
                int count = issueCount.get(rawIssueType) != null ? issueCount.get(rawIssueType) : 0;
                issueCount.put(rawIssueType,++count);
            }
        }

        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(issueCount.entrySet());
        Collections.sort(list,(o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        for(Map.Entry<String,Integer> entry : list){
            result.put(entry.getKey(),entry.getValue());
        }

        return result;
    }

    @Override
    public Object getQualityChangesByCondition(String developer, String timeGranularity, String since, String until, String repoId, String tool,int page,int ps) throws RuntimeException{
        long start = System.currentTimeMillis();
        //因为repo_measure表的commit_time存储时间格式与scan_result的commit_date不同，故需要将repo_measure表的时间日期加1天。
        String repoMeasureUntil = null;
        if(since != null ){
            if(!since.matches("([0-9]+)-([0-9]{2})-([0-9]{2})")){
                throw new RuntimeException(" The input format of since should be like 2019-10-01") ;
            }
        }

        if(until  != null ){
            if(!until.matches("([0-9]+)-([0-9]{2})-([0-9]{2})")){
                throw new RuntimeException(" The input format of until should be like 2019-10-01") ;
            }
            repoMeasureUntil = DateTimeUtil.stringToLocalDate(until).plusDays(1).toString();
        }

        CodeQualityResponse codeQualityResponse = new CodeQualityResponse();


        List<ScanResult> scanResults = scanResultDao.getScanResultByCondition(repoId,since,until,tool,developer);
        if(scanResults == null || scanResults.isEmpty()){
            return codeQualityResponse;
        }



        if(developer==null){
            //----------------------------------------此段代码针对以开发者分类的代码质量集合--------------------------------
            Map<String,List<ScanResult>> developerMap = scanResults.stream().collect(Collectors.groupingBy((ScanResult scanResult)->{
                return scanResult.getDeveloper();
            }));


            JSONObject developerWorkInfo = restInterfaceManager.getDeveloperListByDuration(null,since,until,repoId);
            if(developerWorkInfo == null){
                return "request /measure/repository/duration failed";
            }
            JSONArray developerWorkList = developerWorkInfo.getJSONObject("data").getJSONArray("commitInfoList");

            for(int i = 0; i < developerWorkList.size(); i++){
                DeveloperQuality developerQuality = new DeveloperQuality();
                int devNewIssues = 0;
                int devEliminateIssues = 0;
                int devChangedLines = -1;
                JSONObject developerWork = developerWorkList.getJSONObject(i);
                String developerName = developerWork.getString("author");
                List<ScanResult> scanResultsDev = developerMap.get(developerName);
                //获取总的代码变换行数
                devChangedLines = developerWork.getIntValue("add") + developerWork.getIntValue("del");

                if(scanResultsDev != null){
                    for(ScanResult scanResultDev : scanResultsDev){
                        devNewIssues += scanResultDev.getNew_count();
                        devEliminateIssues += scanResultDev.getEliminated_count();
                    }
                }



                developerQuality.setDeveloperName(developerName);
                developerQuality.setNewIssues(devNewIssues);
                developerQuality.setEliminateIssues(devEliminateIssues);
                //此段代码用于上汽演示，后面需要更改
                if(devChangedLines == 0){
                    devChangedLines = 500;
                }
                //
                if(devChangedLines != 0){
                    developerQuality.setEliminateIssueQualityThroughCalculate(devEliminateIssues,devChangedLines);
                    developerQuality.setAddIssueQualityThroughCalculate(devNewIssues,devChangedLines);
                }else{
                    return "the code  hadn't had any change by  " + developerName;
                }

                codeQualityResponse.addDeveloperQuality(developerQuality);


            }

            List<DeveloperQuality> developersQualities = codeQualityResponse.getDevelopers();

            developersQualities.sort(Comparator.comparing(DeveloperQuality::getDeveloperName));

        }

        if(timeGranularity != null && !timeGranularity.isEmpty()){
            //------------------------------------------此段代码针对以时间粒度拆分的数据集合----------------------------------


            //获取根据时间分类的Map集合
            Map<LocalDate,List<ScanResult>> granularityMap = scanResults.stream().collect(Collectors.groupingBy((ScanResult scanResult)->{
                return DateTimeUtil.dateToLocalDate(scanResult.getCommit_date());
            }));

            List<LocalDate> dates = new ArrayList<>(granularityMap.keySet());
            dates.sort(((o1, o2) -> {
                if(o1.equals(o2)) {
                    return 0;
                }
                return o1.isBefore(o2)?-1:1;
            }));


            List<TimeQuality> timeQualities = getTimeQualitiesByTimeGranularity(timeGranularity,dates,granularityMap,repoId,since,until);
            codeQualityResponse.setQualities(timeQualities);
            codeQualityResponse.setTotalCountQualities(timeQualities.size());

            //对时间粒度进行分页处理
            if(page <= 0){
                if(ps <= 0){
                    codeQualityResponse.setQualities(timeQualities);
                }else{
                    if(ps >= timeQualities.size()){
                        codeQualityResponse.setQualities(timeQualities);
                    }else{
                        codeQualityResponse.setQualities(timeQualities.subList(0,ps));
                    }
                }
            }else{
                if(ps <= 0){
                    ps = 300;
                }
                if(timeQualities.size() >= page*ps){
                    codeQualityResponse.setQualities(timeQualities.subList((page-1)*ps,page*ps));
                }else{
                    if(timeQualities.size() >= (page-1)*ps){
                        codeQualityResponse.setQualities(timeQualities.subList((page-1)*ps,timeQualities.size()));
                    }else {
                        codeQualityResponse.setQualities(Collections.EMPTY_LIST);
                    }
                }
            }
        }

        //---------------------------------------------进行总的缺陷以及缺陷质量统计-------------------------------------
        int totalAddIssues = 0;
        int totalEliminateIssues = 0;
        int totalChangedLines = -1;

        for(ScanResult scanResult : scanResults){
            totalAddIssues += scanResult.getNew_count();
            totalEliminateIssues += scanResult.getEliminated_count();
        }

        totalChangedLines = restInterfaceManagerUtil.getTotalCodeChangedLines(developer,since,repoMeasureUntil,tool,repoId);
        Quality totalQuality = new Quality();
        totalQuality.setNewIssues(totalAddIssues);
        totalQuality.setEliminateIssues(totalEliminateIssues);
        totalQuality.setEliminateIssueQualityThroughCalculate(totalEliminateIssues,totalChangedLines);
        totalQuality.setAddIssueQualityThroughCalculate(totalAddIssues,totalChangedLines);

        codeQualityResponse.setTotalQuality(totalQuality);

        System.out.println((System.currentTimeMillis()-start)*1.0/1000/60);

        return codeQualityResponse;
    }


    private List<TimeQuality> getTimeQualitiesByTimeGranularity(String timeGranularity, List<LocalDate> dates, Map<LocalDate,List<ScanResult>> granularityMap,String repoId,String since,String until){
        List<TimeQuality> tTimeQualities = new LinkedList<>();



        TimeQuality timeQuality = new TimeQuality();

        int dateSize = dates.size();
        LocalDate nextTime = getNextLocalDate(DateTimeUtil.stringToLocalDate(since),timeGranularity);
        int i = 0;
        int addIssues=0;
        int delIssues=0;
        while(i<dateSize) {
            LocalDate localDate = dates.get(i);
            if (localDate.isBefore(nextTime)) {
                List<ScanResult> scanResults = granularityMap.get(localDate);
                for (ScanResult scanResult : scanResults) {
                    if (timeQuality.getDate() == null) {
                        timeQuality.setDate(getPreLocalDate(nextTime,timeGranularity));
                    }
                    addIssues += scanResult.getNew_count();
                    delIssues += scanResult.getEliminated_count();
                }
                i++;
            } else {
                if(timeQuality.getDate() == null){
                    timeQuality.setDate(getPreLocalDate(nextTime,timeGranularity));
                }
                //当条件不符合时，仅表示一个月的统计结束了，下面的代码表示统计加入队列，且此时i不增。
                insertTimeQuality(timeQuality,nextTime,repoId,addIssues,delIssues,tTimeQualities);

                //当插入了一条数据以后对数据进行初始化，且此时i不增
                timeQuality = new TimeQuality();
                addIssues = 0;
                delIssues = 0;
                nextTime =getNextLocalDate(nextTime,timeGranularity);
            }
        }
        //当最后不满一周或者一月时，另外也需做统计插入
        insertTimeQuality(timeQuality,DateTimeUtil.stringToLocalDate(until).plusDays(1),repoId,addIssues,delIssues,tTimeQualities);



        return tTimeQualities;
    }


    private LocalDate getNextLocalDate(LocalDate preLocalDate,String timeGranularity){
        LocalDate nextLocalDate =null;
        if(TimeGranularity.MONTH.getType().equals(timeGranularity)){
            nextLocalDate = preLocalDate.plusMonths(1);
        }else if(TimeGranularity.WEEK.getType().equals(timeGranularity)){
            nextLocalDate = preLocalDate.plusWeeks(1);
        }else if(TimeGranularity.DAY.getType().equals(timeGranularity)){
            nextLocalDate = preLocalDate.plusDays(1);
        }else{
            throw new RuntimeException("please input correct time granularity ,such like day,week and month.");
        }
        return nextLocalDate;
    }

    private LocalDate getPreLocalDate(LocalDate nextLocalDate,String timeGranularity){
        LocalDate preLocalDate =null;
        if(TimeGranularity.MONTH.getType().equals(timeGranularity)){
            preLocalDate = nextLocalDate.minusMonths(1);
        }else if(TimeGranularity.WEEK.getType().equals(timeGranularity)){
            preLocalDate = nextLocalDate.minusWeeks(1);
        }else if(TimeGranularity.DAY.getType().equals(timeGranularity)){
            preLocalDate = nextLocalDate.minusDays(1);
        }else{
            throw new RuntimeException("please input correct time granularity ,such like day,week and month.");
        }
        return preLocalDate;
    }

    private void insertTimeQuality(TimeQuality timeQuality,LocalDate nextTime,String repoId,int addIssues,int delIssues,List<TimeQuality> tTimeQualities){
        int repoAddLines = 0;
        int repoDelLines = 0;
        int timeChangedLines = 0;
        LocalDate since = timeQuality.getDate();
        LocalDate until = nextTime;
        JSONObject codeChangesResponse = restInterfaceManager.getCodeChangesByDurationAndDeveloperName(null, since==null?null:since.toString(), until.toString(), null, repoId);
        if (codeChangesResponse != null && codeChangesResponse.getInteger("code") == 200) {
            JSONObject commitBase = codeChangesResponse.getJSONObject("data");
            repoAddLines = commitBase.getIntValue("addLines");
            repoDelLines = commitBase.getIntValue("delLines");
        } else {
            logger.error("request /measure/developer/code-change failed");
            throw new RuntimeException("request /measure/developer/code-change failed");
        }
        timeChangedLines = repoAddLines + repoDelLines;

        timeQuality.setNewIssues(addIssues);
        timeQuality.setEliminateIssues(delIssues);
        if (timeChangedLines != 0) {
            timeQuality.setEliminateIssueQualityThroughCalculate(delIssues, timeChangedLines);
            timeQuality.setAddIssueQualityThroughCalculate(addIssues, timeChangedLines);
        } else {
            logger.info("the code  hadn't had any change during {} --> {}  ", since, until);
            if (delIssues != 0 || addIssues != 0) {
                logger.error("code did not changed but issues changed");
                throw new RuntimeException("code did not changed but issues changed");
            }
            timeQuality.setEliminateIssueQuality(0);
            timeQuality.setAddIssueQuality(0);
        }
        tTimeQualities.add(timeQuality);

    }

    @Autowired
    private LocationDao locationDao;

    @Override
    public Object getCloneLines(String repoId) {

        List<String> commitIds = issueDao.getCommitId(repoId);

        ArrayList cloneLineList = new ArrayList();

        for(String commit :commitIds){
            cloneLineList.add(getCloneLinesByCommit(repoId,commit));
        }
        return cloneLineList;
    }

    @Override
    public Object getLatestScannedCommitCloneLines(String repoId) {
        String latestCommitId = rawIssueDao.getLatestScannedCommitId(repoId,"clone");
        return getCloneLinesByCommit(repoId,latestCommitId);
    }

    private Integer getCloneLinesByCommit(String repoId, String commitId){
        List<String> rawIssueIdList = rawIssueDao.getRawIssueIdByCommitId(repoId,commitId,"clone");
        int cloneLines = 0;
        for(String rawIssue:rawIssueIdList){
            List<Location> locations = locationDao.getLocations(rawIssue);
            for(Location location: locations){
                int startLine = location.getStart_line();
                int endLine = location.getEnd_line();
                int clone = endLine - startLine + 1;
                cloneLines += clone;
            }
        }
        return cloneLines;
    }


}