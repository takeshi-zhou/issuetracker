package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import cn.edu.fudan.cloneservice.mapper.PackageScanStatusMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public class PackageScanStatusDao {


    PackageScanStatusMapper packageScanStatusMapper;

    public PackageScanStatusDao(PackageScanStatusMapper packageScanStatusMapper){
        this.packageScanStatusMapper = packageScanStatusMapper;
    }

    public String selectPackageScanStatusByRepoIdAndCommitId(String repod_id, String commit_id){
        String res = "NotFound";
        try{
            res = packageScanStatusMapper.selectPackageScanStatusByRepoIdAndCommitId(repod_id,commit_id);
            if(res == null){
                return "NotFound";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  res;
    }

    public void insertPackageScanStatusByRepoIdAndCommitId(String repo_id, String commit_id){
        String status = "Scanned";
        String uuid = UUID.randomUUID().toString();
        packageScanStatusMapper.insertPackageScanStatusByRepoIdAndCommitId(uuid,repo_id,commit_id,status);
    }

}
