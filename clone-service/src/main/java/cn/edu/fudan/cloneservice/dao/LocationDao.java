package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Location;
import cn.edu.fudan.cloneservice.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Repository
public class LocationDao {
    @Autowired
    private LocationMapper locationMapper;

    public List<Location> getLocations(String rawIssueId) {
        return locationMapper.getLocations(rawIssueId);
    }
}
