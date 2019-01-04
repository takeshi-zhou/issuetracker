/**
 * @description:
 * @author: fancying
 * @create: 2019-01-03 14:59
 **/
package cn.edu.fudan.tagservice.dao;

import cn.edu.fudan.tagservice.domain.IgnoreRecord;
import cn.edu.fudan.tagservice.mapper.IgnoreRecodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IgnoreRecodeDao {

    private IgnoreRecodeMapper ignoreRecodeMapper;

    @Autowired
    public void setIgnoreRecodeMapper(IgnoreRecodeMapper ignoreRecodeMapper) {
        this.ignoreRecodeMapper = ignoreRecodeMapper;
    }


    public void insertOneRecord(IgnoreRecord ignoreRecord) {
        ignoreRecodeMapper.insertOneRecord(ignoreRecord);
    }

    public void cancelOneIgnoreRecord(String userId, int level, String type, String repoId) {
        ignoreRecodeMapper.cancelOneIgnoreRecord(userId, level, type, repoId);
    }

    public Integer queryMinIgnoreLevelByUserId(String userId, String type) {
        return ignoreRecodeMapper.queryMinIgnoreLevelByUserId(userId, type);
    }

    public void cancelInvalidRecord(String userId, String type) {
        ignoreRecodeMapper.cancelInvalidRecord(userId, type);
    }

    public IgnoreRecord queryOneRecord(String userId, int level, String type, String repoId) {
        return ignoreRecodeMapper.queryOneRecord(userId, level, type, repoId);
    }

    public List<IgnoreRecord> getIgnoreRecordList(String userId) {
        return ignoreRecodeMapper.getIgnoreRecordList(userId);
    }

    public List<String> getIgnoreTypeListByRepoId(String repoId) {
        return ignoreRecodeMapper.getIgnoreTypeListByRepoId(repoId);
    }
}