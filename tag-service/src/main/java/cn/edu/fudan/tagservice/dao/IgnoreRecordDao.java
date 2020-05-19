/**
 * @description:
 * @author: fancying
 * @create: 2019-01-03 14:59
 **/
package cn.edu.fudan.tagservice.dao;

import cn.edu.fudan.tagservice.domain.IgnoreRecord;
import cn.edu.fudan.tagservice.mapper.IgnoreRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IgnoreRecordDao {

    private IgnoreRecordMapper ignoreRecordMapper;

    @Autowired
    public void setIgnoreRecordMapper(IgnoreRecordMapper ignoreRecordMapper) {
        this.ignoreRecordMapper = ignoreRecordMapper;
    }


    public void insertOneRecord(IgnoreRecord ignoreRecord) {
        ignoreRecordMapper.insertOneRecord(ignoreRecord);
    }

    public void cancelOneIgnoreRecord(String userId, int level, String type, String repoId, String tool) {
        ignoreRecordMapper.cancelOneIgnoreRecord(userId, level, type, repoId, tool);
    }

    public Integer queryMinIgnoreLevelByUserId(String userId, String type) {
        return ignoreRecordMapper.queryMinIgnoreLevelByUserId(userId, type);
    }

    public void cancelInvalidRecord(String userId, String type, String tool) {
        ignoreRecordMapper.cancelInvalidRecord(userId, type, tool);
    }

    public IgnoreRecord queryOneRecord(String userId, int level, String type, String repoId, String tool) {
        return ignoreRecordMapper.queryOneRecord(userId, level, type, repoId, tool);
    }

    public List<IgnoreRecord> getIgnoreRecordList(String userId) {
        return ignoreRecordMapper.getIgnoreRecordList(userId);
    }

    public List<String> getIgnoreTypeListByRepoId(String repoId) {
        return ignoreRecordMapper.getIgnoreTypeListByRepoId(repoId);
    }

    public void deleteIgnoreRecordWhenRepoRemove(String repoId, String accountId) {
        ignoreRecordMapper.deleteIgnoreRecordWhenRepoRemove(repoId, accountId);
    }
}