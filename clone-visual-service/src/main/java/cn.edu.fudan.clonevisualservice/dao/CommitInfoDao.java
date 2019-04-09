package cn.edu.fudan.clonevisualservice.dao;

import cn.edu.fudan.clonevisualservice.domain.PackageInfo;
import cn.edu.fudan.clonevisualservice.mapper.PackageInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommitInfoDao {
    @Autowired
    private PackageInfoMapper packageInfoMapper;


    public CommitInfoDao() {
    }

    public void  testInsert(String repo_id,
                            String commit_id){
        try{
            PackageInfo packageInfo = new PackageInfo("test","test","test","test",1,1);
//            packageInfoMapper.insertPackageNameSetByRepoIdAndCommitId(packageInfo);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
//    public List<PackageInfo> getPackageInfoByRepoIdAndCommitId(String repo_id,
//                                                               String commit_id){
//        return commitInfoMapper.getPackageInfo(repo_id, commit_id);
//    }
//
//    public List<String> getCommitListByRepoId(String repo_id){
//
//        try{
//            String ls = commitInfoMapper.getCommitList(repo_id);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}
