package cn.edu.fudan.cloneservice.controller;


import cn.edu.fudan.cloneservice.service.CloneVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloneVisController {
    private CloneVisService cloneVisService;

    @Autowired
    public void setCloneVisService(CloneVisService cloneVisService) {
        this.cloneVisService = cloneVisService;
    }

    @GetMapping(value = {"/CloneVis"})
    public Object getHistoInfo(@RequestParam("repo_id") String  repo_id){


        String s = cloneVisService.getCommitInfoByRepoId(repo_id);
        System.out.println(s);
        return s;
    }
}
