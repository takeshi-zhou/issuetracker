package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.mapper.PackageNameMapper;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public class PackageNameDao {
    PackageNameMapper packageNameMapper;

    public PackageNameDao(PackageNameMapper packageNameMapper) {
        this.packageNameMapper = packageNameMapper;
    }

    public void  insertPackageName(String repo_id, String commi_id, Set<String> packge_name_set){
        for(String name:packge_name_set){
            String uuid = UUID.randomUUID().toString();
            packageNameMapper.insertPackageNameSetByRepoIdAndCommitId(uuid,repo_id,commi_id, name);
        }
    }
}
