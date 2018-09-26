package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.CloneLocation;
import cn.edu.fudan.cloneservice.mapper.CloneLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Repository
public class CloneLocationDao {

    private CloneLocationMapper cloneLocationMapper;

    @Autowired
    public CloneLocationDao(CloneLocationMapper cloneLocationMapper) {
        this.cloneLocationMapper = cloneLocationMapper;
    }

    public void insertCloneLocationList(List<CloneLocation> list){
        cloneLocationMapper.insertCloneLocationList(list);
    }
}
