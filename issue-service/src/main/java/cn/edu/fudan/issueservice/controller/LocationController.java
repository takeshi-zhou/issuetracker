package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@RestController
@RequestMapping("/Location")
public class LocationController {

    private LocationService locationService;

    @Autowired
    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping(value = {"/add"})
    public Object addLocations(@RequestBody List<Location> list){
        try{
            locationService.insertLocationList(list);
            return new ResponseBean(200,"location add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"location add failed!",null);
        }
    }

    @DeleteMapping(value={"/delete/{projectId}"})
    public Object deleteLocations(@PathVariable("projectId")String projectId){
        try{
            locationService.deleteLocationByProjectId(projectId);
            return new ResponseBean(200,"location delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"location delete failed!",null);
        }
    }

    @GetMapping(value = {"/locationList"})
    public Object getLocations(@RequestParam("rawIssueId")String rawIssueId){
        return locationService.getLocations(rawIssueId);
    }
}
