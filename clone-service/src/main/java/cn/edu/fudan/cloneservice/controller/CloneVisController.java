package cn.edu.fudan.cloneservice.controller;


import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.service.CloneVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CloneVisController {
    private CloneVisService cloneVisService;

    @Autowired
    public void setCloneVisService(CloneVisService cloneVisService) {
        this.cloneVisService = cloneVisService;
    }

    @GetMapping(value = {"/CloneVis"})
    public Object getHistoInfo(@RequestParam("repo_id") String  repo_id,
                                @RequestParam("commit_id") String commit_id) {


        List<PackageInfo> lpi = cloneVisService.getCommitInfoByRepoId(repo_id, commit_id);
        Map<String, List<Integer>> commit_map = new HashMap<>();
        for(PackageInfo pi:lpi){
            List<Integer> package_list = new ArrayList<>();
            package_list.add(pi.getMethod_num());
            package_list.add(pi.getClone_ins_num());
            commit_map.put(pi.getPackage_name(), package_list);
        }
        return commit_map;
    }


}
