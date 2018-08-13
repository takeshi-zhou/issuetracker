package cn.edu.fudan.projectmanager.controller;

import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.domain.ResponseBean;
import cn.edu.fudan.projectmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private ProjectService projectService;

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public Object addProject(HttpServletRequest request, @RequestBody Project project){
        String userToken=request.getHeader("token");
        try{
            projectService.addOneProject(userToken,project.getUrl());
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }

    //get project list
    @GetMapping
    public Object query(HttpServletRequest request){
        String userToken=request.getHeader("token");
        return projectService.getProjectList(userToken);
    }

    @DeleteMapping(value={"/{projectId}"})
    public Object delete(@PathVariable("projectId")String projectId){
        try{
            projectService.remove(projectId);
            return new ResponseBean(200,"project delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"project delete failed!",null);
        }
    }

    //need test
    @PutMapping
    public Object updateProject(@RequestBody Project project){
        try{
            projectService.updateProjectStatus(project);
            return new ResponseBean(200,"project update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"project update failed!",null);
        }
    }

    @GetMapping(value="/repo-path/{project_id}")
    public Object getRepoPath(@PathVariable("project_id")String project_id){
        return projectService.getRepoPath(project_id);
    }

    @GetMapping(value = {"/{project-id}"})
    public Object getProject(@PathVariable("project-id")String project_id){
        return projectService.getProjectByID(project_id);
    }

    @GetMapping(value = "/repo-id")
    public String getRepoId(@RequestParam("project-id")String projectId){
        return projectService.getRepoId(projectId);
    }

}
