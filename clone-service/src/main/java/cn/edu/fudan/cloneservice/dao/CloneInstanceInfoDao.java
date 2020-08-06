package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import cn.edu.fudan.cloneservice.bean.GroupInfo;
import cn.edu.fudan.cloneservice.bean.MethodInfo;
import cn.edu.fudan.cloneservice.bean.ProjectInfo;
import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.mapper.CloneInfoMapper;
import cn.edu.fudan.cloneservice.mapper.CloneInstanceInfoMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CloneInstanceInfoDao {
    CloneInstanceInfoMapper cloneInstanceInfoMapper;

    public CloneInstanceInfoDao(CloneInstanceInfoMapper cloneInstanceInfoMapper) {
        this.cloneInstanceInfoMapper = cloneInstanceInfoMapper;
    }

    public List<CloneInstanceInfo> getCloneInsListByRepoIdAndCommitId(String repo_id, String commit_id){
        List<CloneInstanceInfo> lci = new ArrayList<>();
        try{
            lci = cloneInstanceInfoMapper.selectInfoByCommitIdAndRepoId(repo_id, commit_id);
            return lci;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public ProjectInfo getCloneInfoByRepoIdAndCommitId(String repo_id, String commit_id){
        List<CloneInstanceInfo> lci = new ArrayList<>();
        try{
            lci = cloneInstanceInfoMapper.selectInfoByCommitIdAndRepoId(repo_id, commit_id);
            System.out.println(lci);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Map<Integer, GroupInfo> project = new HashMap<>();
        for (CloneInstanceInfo info: lci) {
            if(project.containsKey(info.getGroupID())){
                MethodInfo method = new MethodInfo("M-*-" + info.getClass_name() + "-*-" + info.getMethod_name());
                project.get(info.getGroupID()).children.add(method);
            }else{
                List<MethodInfo> methodInfoList = new ArrayList<>();
                MethodInfo method = new MethodInfo("M-*-" + info.getClass_name() + "-*-" + info.getMethod_name());
                GroupInfo groupInfo = new GroupInfo(Integer.toString(info.getGroupID()), methodInfoList);
                groupInfo.children.add(method);
                project.put(info.getGroupID(), groupInfo);
            }
        }

        List<GroupInfo> groupList = new ArrayList<>();

        for (Map.Entry<Integer, GroupInfo> entry: project.entrySet()) {
            entry.getValue().name = "G-*-" + entry.getValue().children.size();
            groupList.add(entry.getValue());
        }
        ProjectInfo proInfo = new ProjectInfo("R-*-" + commit_id, groupList);

        return proInfo;

    }
}

