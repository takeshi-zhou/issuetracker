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

    //add
    //post /projects/

    //modify    put
     //   /projects/{id}
    @PostMapping
    public Object addProject(HttpServletRequest request, @RequestParam("url")String url){
        String userToken=request.getHeader("token");
        try{
            projectService.addOneProject(userToken,url);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }

    //get
    //  /projects
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

    @PostMapping(value={"/update"})
    public Object updateProject(@RequestBody Project project){
        try{
            projectService.updateProjectStatus(project);
            return new ResponseBean(200,"project update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"project update failed!",null);
        }
    }

    @GetMapping(value="/repoPath/{project_id}")
    public Object getRepoPath(@PathVariable("project_id")String project_id){
        return projectService.getRepoPath(project_id);
    }

    @GetMapping(value = {"/{project_id}"})
    public Object getProject(@PathVariable("project_id")String project_id){
        return projectService.getProjectByID(project_id);
    }
}
