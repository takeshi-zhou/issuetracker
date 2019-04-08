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

    public void  insertPackageInfo(String repo_id, String commi_id, Map<String, Integer> map_name_mecount, Map<String, Integer> map_clone_dis){
        for(String name:map_name_mecount.keySet()){
            String uuid = UUID.randomUUID().toString();
            int clone_num;
            if(map_clone_dis.containsKey(name)){
                clone_num = map_clone_dis.get(name);
            }else {
                clone_num = 0;
            }
            PackageInfo packageInfo = new PackageInfo(uuid,repo_id,commi_id, name, map_name_mecount.get(name).intValue(), clone_num);
            packageNameMapper.insertPackageNameSetByRepoIdAndCommitId(packageInfo);
        }
    }
    public String selectTest(String repo_id, String commit_id){
        try{
            return packageNameMapper.selectPackageNameSetByRepoIdAndCommitId(repo_id, commit_id);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "error";
    }


}
