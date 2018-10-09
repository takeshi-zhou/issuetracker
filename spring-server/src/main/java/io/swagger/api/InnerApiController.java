package io.swagger.api;

import io.swagger.model.IssueJSONObject;
import io.swagger.model.IssueList;
import java.util.List;
import io.swagger.model.Project;
import io.swagger.model.RawIssueList;
import io.swagger.model.RequestBody2;
import io.swagger.model.ResponseBean;
import io.swagger.model.Scan;
import io.swagger.model.TagList;
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
public class InnerApiController implements InnerApi {

    private static final Logger log = LoggerFactory.getLogger(InnerApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public InnerApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResponseBean> addIssues(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueList issueList) {
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

    public ResponseEntity<ResponseBean> addMultiTaggedItem(@ApiParam(value = " " ,required=true )  @Valid @RequestBody List<RequestBody2> requestBody) {
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

    public ResponseEntity<ResponseBean> addRawIssues(@ApiParam(value = " " ,required=true )  @Valid @RequestBody RawIssueList list) {
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

    public ResponseEntity<String> addscan(@ApiParam(value = "scan json object" ,required=true )  @Valid @RequestBody Scan scan) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<String>(objectMapper.readValue("\"\"", String.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ResponseBean> deleteIssues(@ApiParam(value = "",required=true) @PathVariable("repoId") String repoId) {
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

    public ResponseEntity<ResponseBean> deleteRawIssue(@ApiParam(value = " ",required=true) @PathVariable("repoId") String repoId) {
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

    public ResponseEntity<ResponseBean> deleteScans(@ApiParam(value = "a repo id",required=true) @PathVariable("repoId") String repoId) {
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

    public ResponseEntity<ResponseBean> deleteTagByProjectId(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "project-id", required = true) String projectId) {
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

    public ResponseEntity<ResponseBean> deleteTagged(@ApiParam(value = " " ,required=true )  @Valid @RequestBody List<String> itemIds) {
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

    public ResponseEntity<List<String>> getItemIds(@ApiParam(value = " " ,required=true )  @Valid @RequestBody List<String> tagIds) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<String>>(objectMapper.readValue("[ \"\", \"\" ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<String>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<String> getLatestScannedCommitId(@NotNull @ApiParam(value = "a repo_id id", required = true) @Valid @RequestParam(value = "repo_id", required = true) String repoId,@NotNull @ApiParam(value = "category", required = true) @Valid @RequestParam(value = "category", required = true) String category) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<String>(objectMapper.readValue("\"\"", String.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Project> getProject(@ApiParam(value = "project that user want to get",required=true) @PathVariable("projectId") String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Project>(objectMapper.readValue("{  \"scan_status\" : \"scan_status\",  \"description\" : \"description\",  \"language\" : \"language\",  \"uuid\" : \"uuid\",  \"url\" : \"url\",  \"vcs_type\" : \"vcs_type\",  \"till_commit_time\" : \"till_commit_time\",  \"account_id\" : \"account_id\",  \"prev_scan_commit\" : \"prev_scan_commit\",  \"repo_id\" : \"repo_id\",  \"name\" : \"name\",  \"download_status\" : \"download_status\",  \"last_scan_time\" : \"last_scan_time\"}", Project.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Project>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Project>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<String>> getProjectIds(@ApiParam(value = "account_id that user want to get") @Valid @RequestParam(value = "account_id", required = false) String accountId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<String>>(objectMapper.readValue("[ \"\", \"\" ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<String>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<RawIssueList> getRawIssues(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "commit_id", required = true) String commitId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "category", required = true) String category) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<RawIssueList>(objectMapper.readValue("\"\"", RawIssueList.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<RawIssueList>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<RawIssueList>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<String> getRepoId(@NotNull @ApiParam(value = "project id", required = true) @Valid @RequestParam(value = "project-id", required = true) String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<String>(objectMapper.readValue("\"\"", String.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<String> getRepoPath(@ApiParam(value = "project id",required=true) @PathVariable("project_id") String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<String>(objectMapper.readValue("\"\"", String.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Scan> getScannedCommits(@NotNull @ApiParam(value = "a repo id", required = true) @Valid @RequestParam(value = "repo_id", required = true) String repoId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Scan>(objectMapper.readValue("{  \"start_time\" : \"start_time\",  \"project_id\" : \"project_id\",  \"name\" : \"name\",  \"end_time\" : \"end_time\",  \"result_summary\" : \"result_summary\",  \"uuid\" : \"uuid\",  \"commit_id\" : \"commit_id\",  \"status\" : \"status\"}", Scan.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Scan>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Scan>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<TagList> getTags(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "item-id", required = true) String itemId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TagList>(objectMapper.readValue("\"\"", TagList.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TagList>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TagList>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ResponseBean> mapping(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueJSONObject requestParam) {
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

    public ResponseEntity<ResponseBean> updateIssues(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueList issueList) {
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

    public ResponseEntity<ResponseBean> updateProject(@ApiParam(value = " message about project " ,required=true )  @Valid @RequestBody Project project) {
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

    public ResponseEntity<ResponseBean> updateRawIssues(@ApiParam(value = " " ,required=true )  @Valid @RequestBody RawIssueList list) {
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

    public ResponseEntity<ResponseBean> updateScan(@ApiParam(value = "a scan object" ,required=true )  @Valid @RequestBody Scan scan) {
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

}
