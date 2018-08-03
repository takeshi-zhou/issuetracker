package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class LocationServiceImpl implements LocationService {

    private LocationDao locationDao;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    @Override
    public void insertLocationList(List<Location> list) {
        locationDao.insertLocationList(list);
    }

    @Override
    public void deleteLocationByProjectId(String projectId) {
         locationDao.deleteLocationByProjectId(projectId);
    }

    @Override
    public List<Location> getLocations(String rawIssueId) {
        return locationDao.getLocations(rawIssueId);
    }
}
