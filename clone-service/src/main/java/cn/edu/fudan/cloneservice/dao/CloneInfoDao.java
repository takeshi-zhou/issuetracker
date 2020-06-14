package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.mapper.CloneInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zyh
 * @date 2020/5/29
 */
@Repository
public class CloneInfoDao {

    @Autowired
    private CloneInfoMapper cloneInfoMapper;

    public void insertCloneInfo(List<CloneInfo> cloneInfoList){
        cloneInfoMapper.insertCloneInfo(cloneInfoList);
    }

    public void deleteCloneInfo(String repoId){
        cloneInfoMapper.deleteCloneInfo(repoId);
    }
}
