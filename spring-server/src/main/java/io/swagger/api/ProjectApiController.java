package io.swagger.api;

import io.swagger.model.AddProjectJson;
import io.swagger.model.Project;
import io.swagger.model.ResponseBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

@Controller
public class ProjectApiController implements ProjectApi {

    private static final Logger log = LoggerFactory.getLogger(ProjectApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ProjectApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResponseBean> addProject(@ApiParam(value = "project that user want to add" ,required=true )  @Valid @RequestBody AddProjectJson project,@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ResponseBean>(objectMapper.readValue("{  \"msg\" : \"msg\",  \"code\" : 0,  \"data\" : \"{}\"}", ResponseBean.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ResponseBean>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ResponseBean>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ResponseBean> delete(@ApiParam(value = "project that user want to delet",required=true) @PathVariable("projectId") String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ResponseBean>(objectMapper.readValue("{  \"msg\" : \"msg\",  \"code\" : 0,  \"data\" : \"{}\"}", ResponseBean.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ResponseBean>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ResponseBean>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Project>> filter(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "keyWord", required = true) String keyWord) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Project>>(objectMapper.readValue("[ {  \"scan_status\" : \"scan_status\",  \"description\" : \"description\",  \"language\" : \"language\",  \"uuid\" : \"uuid\",  \"url\" : \"url\",  \"vcs_type\" : \"vcs_type\",  \"till_commit_time\" : \"till_commit_time\",  \"account_id\" : \"account_id\",  \"prev_scan_commit\" : \"prev_scan_commit\",  \"repo_id\" : \"repo_id\",  \"name\" : \"name\",  \"download_status\" : \"download_status\",  \"last_scan_time\" : \"last_scan_time\"}, {  \"scan_status\" : \"scan_status\",  \"description\" : \"description\",  \"language\" : \"language\",  \"uuid\" : \"uuid\",  \"url\" : \"url\",  \"vcs_type\" : \"vcs_type\",  \"till_commit_time\" : \"till_commit_time\",  \"account_id\" : \"account_id\",  \"prev_scan_commit\" : \"prev_scan_commit\",  \"repo_id\" : \"repo_id\",  \"name\" : \"name\",  \"download_status\" : \"download_status\",  \"last_scan_time\" : \"last_scan_time\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Project>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Project>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Project>> query(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "type", required = true) String type) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Project>>(objectMapper.readValue("[ {  \"scan_status\" : \"scan_status\",  \"description\" : \"description\",  \"language\" : \"language\",  \"uuid\" : \"uuid\",  \"url\" : \"url\",  \"vcs_type\" : \"vcs_type\",  \"till_commit_time\" : \"till_commit_time\",  \"account_id\" : \"account_id\",  \"prev_scan_commit\" : \"prev_scan_commit\",  \"repo_id\" : \"repo_id\",  \"name\" : \"name\",  \"download_status\" : \"download_status\",  \"last_scan_time\" : \"last_scan_time\"}, {  \"scan_status\" : \"scan_status\",  \"description\" : \"description\",  \"language\" : \"language\",  \"uuid\" : \"uuid\",  \"url\" : \"url\",  \"vcs_type\" : \"vcs_type\",  \"till_commit_time\" : \"till_commit_time\",  \"account_id\" : \"account_id\",  \"prev_scan_commit\" : \"prev_scan_commit\",  \"repo_id\" : \"repo_id\",  \"name\" : \"name\",  \"download_status\" : \"download_status\",  \"last_scan_time\" : \"last_scan_time\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Project>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Project>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
