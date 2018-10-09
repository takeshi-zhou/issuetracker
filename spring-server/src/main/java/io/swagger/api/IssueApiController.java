package io.swagger.api;

import io.swagger.model.InlineResponse200;
import io.swagger.model.InlineResponse2001;
import io.swagger.model.IssueFilterRequestParam;
import io.swagger.model.IssuesMsg;
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
public class IssueApiController implements IssueApi {

    private static final Logger log = LoggerFactory.getLogger(IssueApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public IssueApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<InlineResponse200> filterIssues(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueFilterRequestParam requestParam) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<InlineResponse200>(objectMapper.readValue("{  \"totalPage\" : 0,  \"totalCount\" : 6}", InlineResponse200.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<InlineResponse200>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<InlineResponse200>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<InlineResponse2001> getDashBoardInfo(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "duration", required = true) String duration,@ApiParam(value = "") @Valid @RequestParam(value = "project_id", required = false) String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<InlineResponse2001>(objectMapper.readValue("{  \"newIssueCount\" : 0,  \"eliminatedIssueCount\" : 6,  \"remainingIssueCount\" : 1}", InlineResponse2001.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<InlineResponse2001>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<InlineResponse2001>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<String>> getExistIssueTypes() {
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

    public ResponseEntity<IssuesMsg> getIssues(@NotNull @ApiParam(value = "a project id", required = true) @Valid @RequestParam(value = "project-id", required = true) String projectId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "page", required = true) String page,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "size", required = true) String size,@NotNull @ApiParam(value = "category", required = true) @Valid @RequestParam(value = "category", required = true) String category) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<IssuesMsg>(objectMapper.readValue("{  \"totalPage\" : \"totalPage\",  \"start\" : \"start\",  \"issueList\" : [ \"\", \"\" ],  \"totalCount\" : \"totalCount\"}", IssuesMsg.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<IssuesMsg>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<IssuesMsg>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<InlineResponse2001> getStatisticalResults(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "month", required = true) String month,@ApiParam(value = "") @Valid @RequestParam(value = "project_id", required = false) String projectId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<InlineResponse2001>(objectMapper.readValue("{  \"newIssueCount\" : 0,  \"eliminatedIssueCount\" : 6,  \"remainingIssueCount\" : 1}", InlineResponse2001.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<InlineResponse2001>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<InlineResponse2001>(HttpStatus.NOT_IMPLEMENTED);
    }

}
