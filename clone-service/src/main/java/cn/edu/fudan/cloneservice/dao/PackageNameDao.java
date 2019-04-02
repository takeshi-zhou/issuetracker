package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.mapper.PackageNameMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
public class PackageNameDao {
    PackageNameMapper packageNameMapper;

    public PackageNameDao(PackageNameMapper packageNameMapper) {
        this.packageNameMapper = packageNameMapper;
    }

    public void  insertPackageName(String repo_id, String commi_id, Map<String, Integer> map_name_mecount){
        for(String name:map_name_mecount.keySet()){
            String uuid = UUID.randomUUID().toString();
            PackageInfo packageInfo = new PackageInfo(uuid,repo_id,commi_id, name, map_name_mecount.get(name).intValue());
            packageNameMapper.insertPackageNameSetByRepoIdAndCommitId(packageInfo);
        }
    }
}
