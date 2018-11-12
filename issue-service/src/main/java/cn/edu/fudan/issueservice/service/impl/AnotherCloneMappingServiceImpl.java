package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.domain.RawIssue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WZY
 * @version 1.0
 **/
public class AnotherCloneMappingServiceImpl extends CloneMappingServiceImpl {

    private void updateIssue(Map<String,List<RawIssue>> intersectionGroup){
        for(Map.Entry<String,List<RawIssue>> entry:intersectionGroup.entrySet()){

        }
    }

    private  Map<String,List<RawIssue>>  differentSet(Map<String,List<RawIssue>> group,Map<String,List<RawIssue>> intersectionGroup){
        Map<String,List<RawIssue>> diff=new HashMap<>();
        for(String groupId:intersectionGroup.keySet()){
            if(!group.containsKey(groupId))
                diff.put(groupId,group.get(groupId));
        }
        return diff;
    }

    private Map<String,List<RawIssue>> intersectionGroup(Map<String,List<RawIssue>> oldGroup,Map<String,List<RawIssue>> newGroup){
        Map<String,List<RawIssue>> intersection=new HashMap<>();
        for(Map.Entry<String,List<RawIssue>> entry:oldGroup.entrySet()){
            String oldGroupId=entry.getKey();
            if(newGroup.containsKey(oldGroupId)){
                intersection.put(oldGroupId,unionRawIssue(oldGroup.get(oldGroupId),newGroup.get(oldGroupId)));
            }
        }
        return intersection;
    }

    private List<RawIssue> unionRawIssue(List<RawIssue> oldList,List<RawIssue> newList){
        List<RawIssue> unionList=new ArrayList<>(oldList);
        for(RawIssue newRawIssue:newList){
            if(!contains(unionList,newRawIssue)){
                unionList.add(newRawIssue);
            }
        }
        return unionList;
    }

    private boolean contains(List<RawIssue> list,RawIssue rawIssue){
        return true;
    }


    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category, String committer) {
        Date date=new Date();
        if (pre_commit_id.equals(current_commit_id)) {
            //第一次所有的group都是新增的Issue
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(category,current_commit_id);
            if (rawIssues == null || rawIssues.isEmpty())
                return;
            Map<String,List<RawIssue>> map=rawIssues.stream().collect(Collectors.groupingBy(RawIssue::getType));
           // newCloneInsert(map,map.keySet(),repo_id,current_commit_id,category,committer,date);
        }else{
            List<RawIssue> rawIssues1 = rawIssueDao.getRawIssueByCommitIDAndCategory(category,pre_commit_id);//旧
            List<RawIssue> rawIssues2 = rawIssueDao.getRawIssueByCommitIDAndCategory(category,current_commit_id);//新
            if (rawIssues2 == null || rawIssues1.isEmpty())
                return;
            Map<String,List<RawIssue>> map1=rawIssues1.stream().collect(Collectors.groupingBy(RawIssue::getType));
            Map<String,List<RawIssue>> map2=rawIssues2.stream().collect(Collectors.groupingBy(RawIssue::getType));
            Map<String,List<RawIssue>> intersectionGroup=intersectionGroup(map1,map2);
            Map<String,List<RawIssue>> newGroups=differentSet(map2,intersectionGroup);
            Map<String,List<RawIssue>> removedGroups=differentSet(map1,intersectionGroup);
            updateIssue(intersectionGroup);
            //newCloneInsert(newGroups,newGroups.keySet(),repo_id,current_commit_id,category,committer,date);
        }
    }
}
