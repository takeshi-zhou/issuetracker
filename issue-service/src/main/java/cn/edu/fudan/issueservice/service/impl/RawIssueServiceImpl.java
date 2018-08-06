package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.service.RawIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class RawIssueServiceImpl implements RawIssueService {

    private RawIssueDao rawIssueDao;

    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    private LocationDao locationDao;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    /**
     * 插入rawIssue列表，同时会插入里面包含的locations
     * @author WZY
     * @param list rawIssue的列表，里面包含了locations
     */
    @Override
    public void insertRawIssueList(List<RawIssue> list) {
        List<Location> locations=new ArrayList<>();
        for(RawIssue rawIssue:list){
            locations.addAll(rawIssue.getLocations());
        }
        rawIssueDao.insertRawIssueList(list);
        locationDao.insertLocationList(locations);
    }

    /**
     * 删除rawIssue以及对应的locations
     * @author WZY
     * @param projectId 项目的id
     */
    @Override
    public void deleteRawIssueByProjectId(String projectId) {
        locationDao.deleteLocationByProjectId(projectId);
        rawIssueDao.deleteRawIssueByProjectId(projectId);
    }

    @Override
    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueDao.batchUpdateIssueId(list);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitID(String commit_id) {
        return rawIssueDao.getRawIssueByCommitID(commit_id);
    }

    @Override
    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueDao.getRawIssueByIssueId(issueId);
    }
}
