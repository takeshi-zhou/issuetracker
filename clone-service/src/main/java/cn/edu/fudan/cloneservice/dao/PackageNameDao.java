package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.mapper.PackageNameMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
public class PackageNameDao {
    PackageNameMapper packageNameMapper;

    public PackageNameDao(PackageNameMapper packageNameMapper) {
        this.packageNameMapper = packageNameMapper;
    }

    public void  insertPackageInfo(String repo_id, String commi_id, Map<String, List> map_name_method_file_count, List<Map> list_distr)
    {

            Map<String, Integer> ins_map = list_distr.get(0);
            Map<String, Map> line_map = list_distr.get(1);
            for(String name:map_name_method_file_count.keySet()){
                String uuid = UUID.randomUUID().toString();
                int clone_ins_num;
                if(ins_map.containsKey(name)){
                    clone_ins_num = ins_map.get(name);
                }else {
                    clone_ins_num = 0;
                }
                List<Integer> list = map_name_method_file_count.get(name);
//            assert (list.size() == 2);
                int clone_ins_line = 0;
                if(line_map.containsKey(name)){
                    Map<String, Set> submap = line_map.get(name);
                    for(String path:submap.keySet()){
                        clone_ins_line += submap.get(path).size();
                    }
                }
                PackageInfo packageInfo = new PackageInfo(uuid,repo_id,commi_id, name, list.get(1), list.get(0), clone_ins_num, clone_ins_line, 0);
                packageNameMapper.insertPackageNameSetByRepoIdAndCommitId(packageInfo);
            }

    }
    public List<PackageInfo> getPackageInfoByRepoIdAndCommitId(String repo_id, String commit_id){
        return packageNameMapper.selectPackageNameSetByRepoIdAndCommitId(repo_id, commit_id);
    }


    public String selectTest(String repo_id, String commit_id){
        try{
            List<PackageInfo> lpi =  packageNameMapper.selectPackageNameSetByRepoIdAndCommitId(repo_id, commit_id);

            return lpi.get(0).getPackage_name();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "error";
    }


}
