package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.JavaNcss;
import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.Function;
import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.domain.core.FileMeasure;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.mapper.FileMeasureMapper;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import cn.edu.fudan.measureservice.util.JGitHelper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
}