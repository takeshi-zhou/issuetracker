package cn.edu.fudan.cloneservice.scan.dao;

import cn.edu.fudan.cloneservice.scan.domain.CloneLocation;
import cn.edu.fudan.cloneservice.scan.mapper.CloneLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zyh
 * @date 2020/5/27
 */
@Repository
public class CloneLocationDao {

    private CloneLocationMapper cloneLocationMapper;

    @Autowired
    private void setCloneLocationMapper(CloneLocationMapper cloneLocationMapper) {
        this.cloneLocationMapper = cloneLocationMapper;
    }

    public void insertCloneLocations(List<CloneLocation> cloneLocations){
        cloneLocationMapper.insertCloneLocationList(cloneLocations);
    }

    public List<CloneLocation> getCloneLocations(String repoId, String commitId){
        return cloneLocationMapper.getCloneLocations(repoId, commitId);
    }

    public void deleteCloneLocations(String repoId){
        cloneLocationMapper.deleteCloneLocations(repoId);
    }

}
