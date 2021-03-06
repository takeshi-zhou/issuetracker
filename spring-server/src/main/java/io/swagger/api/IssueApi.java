/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.2).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.InlineResponse200;
import io.swagger.model.InlineResponse2001;
import io.swagger.model.IssueCountPo;
import io.swagger.model.IssueFilterRequestParam;
import io.swagger.model.IssueParam;
import io.swagger.model.IssueStatisticInfo;
import io.swagger.model.IssuesMsg;
import io.swagger.model.ResponseBean;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

@Api(value = "issue", description = "the issue API")
public interface IssueApi {

    @ApiOperation(value = "filter of issues", nickname = "filterIssues", notes = "", response = InlineResponse200.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = InlineResponse200.class),
        @ApiResponse(code = 401, message = "failed operation") })
    @RequestMapping(value = "/issue/filter",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<InlineResponse200> filterIssues(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueFilterRequestParam requestParam);


    @ApiOperation(value = "get project statistical info ", nickname = "getAvgEliminatedTimeAndMaxAliveTime", notes = "", response = IssueStatisticInfo.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = IssueStatisticInfo.class),
        @ApiResponse(code = 400, message = "failed") })
    @RequestMapping(value = "/issue/project-statistical-info",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<IssueStatisticInfo> getAvgEliminatedTimeAndMaxAliveTime(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "project_id", required = true) String projectId,@ApiParam(value = "", defaultValue = "bug") @Valid @RequestParam(value = "category", required = false, defaultValue="bug") String category);


    @ApiOperation(value = "get dashboard info ", nickname = "getDashBoardInfo", notes = "", response = InlineResponse2001.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = InlineResponse2001.class),
        @ApiResponse(code = 400, message = "failed") })
    @RequestMapping(value = "/issue/dashboard",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<InlineResponse2001> getDashBoardInfo(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "duration", required = true) String duration,@ApiParam(value = "") @Valid @RequestParam(value = "project_id", required = false) String projectId);


    @ApiOperation(value = "get Exist IssueTypes", nickname = "getExistIssueTypes", notes = "", response = String.class, responseContainer = "List", tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "failed operation") })
    @RequestMapping(value = "/issue/issue-types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<String>> getExistIssueTypes();


    @ApiOperation(value = "get Issues by project_id,page and size", nickname = "getIssues", notes = "", response = IssuesMsg.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = IssuesMsg.class),
        @ApiResponse(code = 401, message = "failed operation") })
    @RequestMapping(value = "/issue",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<IssuesMsg> getIssues(@NotNull @ApiParam(value = "a project id", required = true) @Valid @RequestParam(value = "project_id", required = true) String projectId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "page", required = true) String page,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "size", required = true) String size,@NotNull @ApiParam(value = "category", required = true) @Valid @RequestParam(value = "category", required = true) String category);


    @ApiOperation(value = "get new trend", nickname = "getNewTrend", notes = "", response = IssueCountPo.class, responseContainer = "List", tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = IssueCountPo.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "failed") })
    @RequestMapping(value = "/issue/statistical-results-fix",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<IssueCountPo>> getNewTrend(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "month", required = true) Integer month,@ApiParam(value = "" ,required=true) @RequestHeader(value="token", required=true) String token,@ApiParam(value = "") @Valid @RequestParam(value = "project_id", required = false) String projectId,@ApiParam(value = "", defaultValue = "bug") @Valid @RequestParam(value = "category", required = false, defaultValue="bug") String category);


    @ApiOperation(value = "get specific issues", nickname = "getSpecificIssues", notes = "", response = InlineResponse200.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = InlineResponse200.class),
        @ApiResponse(code = 401, message = "failed operation") })
    @RequestMapping(value = "/issue/specific-issues",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<InlineResponse200> getSpecificIssues(@ApiParam(value = "" ,required=true )  @Valid @RequestBody IssueParam issueParam,@ApiParam(value = "" ,required=true) @RequestHeader(value="token", required=true) String token);


    @ApiOperation(value = "get Statistical Results ", nickname = "getStatisticalResults", notes = "", response = InlineResponse2001.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = InlineResponse2001.class),
        @ApiResponse(code = 400, message = "failed") })
    @RequestMapping(value = "/issue/statistical-results",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<InlineResponse2001> getStatisticalResults(@ApiParam(value = "get userToken from Header" ,required=true) @RequestHeader(value="token", required=true) String token,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "month", required = true) String month,@ApiParam(value = "") @Valid @RequestParam(value = "project_id", required = false) String projectId);


    @ApiOperation(value = "update priority", nickname = "updatePriority", notes = "", response = ResponseBean.class, tags={ "issue-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = ResponseBean.class),
        @ApiResponse(code = 401, message = "failed operation", response = ResponseBean.class) })
    @RequestMapping(value = "/issue/priority/{issue-id}",
        produces = { "application/json" }, 
        method = RequestMethod.PUT)
    ResponseEntity<ResponseBean> updatePriority(@ApiParam(value = "",required=true) @PathVariable("issue-id") String issueId);

}
