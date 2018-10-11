/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.ResponseBean;
import io.swagger.model.ScanJSONObject;
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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

@Api(value = "scan", description = "the scan API")
public interface ScanApi {

    @ApiOperation(value = "Send Scan Message", nickname = "addScan", notes = " ", response = ResponseBean.class, tags={ "scan-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = ResponseBean.class),
        @ApiResponse(code = 401, message = "failed operation", response = ResponseBean.class) })
    @RequestMapping(value = "/scan",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<ResponseBean> addScan(@ApiParam(value = "get projectId and commitId from  JSONObject" ,required=true )  @Valid @RequestBody ScanJSONObject requestParam);


    @ApiOperation(value = "get Commits", nickname = "getCommits", notes = " ", tags={ "scan-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation"),
        @ApiResponse(code = 401, message = "failed operation") })
    @RequestMapping(value = "/scan/commits",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Void> getCommits(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "project_id", required = true) String projectId,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "category", required = true) String category,@ApiParam(value = "", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") String page,@ApiParam(value = "", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") String size,@ApiParam(value = "", defaultValue = "false") @Valid @RequestParam(value = "is_whole", required = false, defaultValue="false") String isWhole);

}