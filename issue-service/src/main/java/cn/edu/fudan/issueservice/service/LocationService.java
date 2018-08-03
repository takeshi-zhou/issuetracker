package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.domain.Location;

import java.util.List;

public interface LocationService {

    void insertLocationList(List<Location> list);

    void deleteLocationByProjectId(String projectId);

    List<Location> getLocations(String rawIssueId);
}
