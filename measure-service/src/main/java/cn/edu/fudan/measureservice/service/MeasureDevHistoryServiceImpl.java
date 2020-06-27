package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.mapper.FileMeasureMapper;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-11 10:48
 **/
@Slf4j
@Service
public class MeasureDevHistoryServiceImpl implements MeasureDevHistoryService {

    private Logger logger = LoggerFactory.getLogger(MeasureDevHistoryServiceImpl.class);

    private RestInterfaceManager restInterfaceManager;
    private RepoMeasureMapper repoMeasureMapper;
    private PackageMeasureMapper packageMeasureMapper;
    private FileMeasureMapper fileMeasureMapper;

    public MeasureDevHistoryServiceImpl(RestInterfaceManager restInterfaceManager, RepoMeasureMapper repoMeasureMapper, PackageMeasureMapper packageMeasureMapper, FileMeasureMapper fileMeasureMapper) {
        this.restInterfaceManager = restInterfaceManager;
        this.repoMeasureMapper = repoMeasureMapper;
        this.packageMeasureMapper = packageMeasureMapper;
        this.fileMeasureMapper = fileMeasureMapper;
    }

    @Override
    public Object getDevHistoryCommitInfo(String repoId, String beginDate, String endDate) {
        List<Map<String, Object>> result;
        result = fileMeasureMapper.getDevHistoryCommitInfo(repoId,beginDate,endDate);
        for (int i = 0; i < result.size(); i++){
            Map<String, Object> map;
            map = result.get(i);
            //将数据库中timeStamp/dateTime类型转换成指定格式的字符串 map.get("commit_time") 这个就是数据库中dateTime类型
            String commit_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(map.get("commit_time"));
            map.put("commit_time",commit_time);
        }
        return result;
    }

    @Override
    public Object getDevHistoryFileInfo(String commitId) {
        String currentCommitId = commitId;
        List<Map<String, Object>> fileInfoList = fileMeasureMapper.getDevHistoryFileInfo(currentCommitId);
        for (int i = 0; i < fileInfoList.size(); i++){
            Map<String, Object> map;
            map = fileInfoList.get(i);
            Integer currentCcn = (Integer) map.get("ccn");
            Integer diffCcn = (Integer) map.get("diff_ccn");
            map.put("lastCcn", currentCcn - diffCcn);
        }
        return fileInfoList;
    }
}