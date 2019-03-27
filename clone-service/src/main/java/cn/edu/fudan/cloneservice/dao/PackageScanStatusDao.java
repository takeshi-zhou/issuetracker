package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import cn.edu.fudan.cloneservice.mapper.PackageScanStatusMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PackageScanStatusDao {


    PackageScanStatusMapper packageScanStatusMapper;

    public PackageScanStatusDao(PackageScanStatusMapper packageScanStatusMapper){
        this.packageScanStatusMapper = packageScanStatusMapper;
    }

    public String selectPackageScanStatusByRepoIdAndCommitId(String repod_id, String commit_id){
//        return  packageScanStatusMapper.selectPackageScanStatusByRepoIdAndCommitId(repod_id,commit_id);
        return packageScanStatusMapper.selectAllStatus();
    }

}
