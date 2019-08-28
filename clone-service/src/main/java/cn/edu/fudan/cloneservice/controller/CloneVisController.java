package cn.edu.fudan.cloneservice.controller;


import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import cn.edu.fudan.cloneservice.bean.GroupInfo;
import cn.edu.fudan.cloneservice.bean.ProjectInfo;
import cn.edu.fudan.cloneservice.dao.CloneInfoDao;
import cn.edu.fudan.cloneservice.dao.CloneInstanceInfoDao;
import cn.edu.fudan.cloneservice.dao.CommitDao;
import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.domain.Commit;
import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.service.CloneVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@EnableAutoConfiguration
public class CloneVisController {
//    private CloneVisService cloneVisService;
//    @Autowired
//    private CloneInfoDao cloneInfo;
//
//    @Autowired
//    private CloneInstanceInfoDao cloneInstanceInfo;
//
//    @Autowired
//    private CommitDao commitDao;

//    @Autowired
//    public void setCloneVisService(CloneVisService cloneVisService) {
//        this.cloneVisService = cloneVisService;
//    }

    //depricated
//    @GetMapping(value = {"/CloneVis"})
//    public Object getHistoInfo(@RequestParam("repo_id") String  repo_id,
//                                @RequestParam("commit_id") String commit_id) {
//
//
//        List<PackageInfo> lpi = cloneVisService.getCommitInfoByRepoId(repo_id, commit_id);
//        Map<String, List<Integer>> commit_map = new HashMap<>();
//        for(PackageInfo pi:lpi){
//            List<Integer> package_list = new ArrayList<>();
//            package_list.add(pi.getFile_num());
//            package_list.add(pi.getMethod_num());
//            package_list.add(pi.getClone_ins_num());
//            commit_map.put(pi.getPackage_name(), package_list);
//        }
//        return commit_map;
//    }

//    @GetMapping(value = {"/CloneVis"})
//    public Object getHistoInfo(@RequestParam("repo_id") String  repo_id,
//                               @RequestParam("commit_id") String commit_id) {
//        List<String> l_commit = commitDao.getCommitList(repo_id);
////        System.out.println(l_commit.size());
//        List<Map> res_list = new ArrayList<>();
//        for(String commit:l_commit){
//            List<PackageInfo> lpi = cloneVisService.getCommitInfoByRepoId(repo_id, commit);
//            Map<String, List<Integer>> commit_map = new HashMap<>();
//            for(PackageInfo pi:lpi){
//                List<Integer> package_list = new ArrayList<>();
//                package_list.add(pi.getFile_num());
//                package_list.add(pi.getMethod_num());
//                package_list.add(pi.getClone_ins_num());
//                commit_map.put(pi.getPackage_name(), package_list);
//            }
//            res_list.add(commit_map);
//        }
//        return res_list;
//    }


//    @GetMapping(value = {"/CloneInfo"})
//    public Object getCloneInfo(@RequestParam("repo_id") String  repo_id,
//                               @RequestParam("commit_id") String commit_id) {
//
//        List<CloneInfo> lci = new ArrayList<>();
//        lci = cloneInfo.getCloneInfoByRepoIdAndCommitId(repo_id, commit_id);
//
//        return lci;
//    }
//    @GetMapping(value = {"/CloneInstanceInfoOld"})
//    public Object getInfoOld(@RequestParam("repo_id") String  repo_id,
//                          @RequestParam("commit_id") String commit_id) {
//        List<String> l_commit = commitDao.getCommitList(repo_id);
//        List<ProjectInfo> res_list = new ArrayList<>();
////        for(String commit:l_commit){
////            ProjectInfo proInfo;
////            proInfo = cloneInstanceInfo.getCloneInfoByRepoIdAndCommitId(repo_id, commit);
////            res_list.add(proInfo);
////        }
//
////        List<CloneInstanceInfo> lci = new ArrayList<>();
////        Map<Integer, GroupInfo> result = new HashMap<>();
//        ProjectInfo proInfo;
//        proInfo = cloneInstanceInfo.getCloneInfoByRepoIdAndCommitId(repo_id, commit_id);
////
//
////        return res_list;
//        return proInfo;
//    }

//    @GetMapping(value = {"/CloneInstanceInfo"})
//    public Object getInfo(@RequestParam("repo_id") String  repo_id,
//                               @RequestParam("commit_id") String commit_id) {
//        List<String> l_commit = commitDao.getCommitList(repo_id);
//        List<ProjectInfo> res_list = new ArrayList<>();
//        for(String commit:l_commit){
//            ProjectInfo proInfo;
//            proInfo = cloneInstanceInfo.getCloneInfoByRepoIdAndCommitId(repo_id, commit);
//            res_list.add(proInfo);
//        }
//
//
//
////
//
//        return res_list;
////        return proInfo;
//    }
}
