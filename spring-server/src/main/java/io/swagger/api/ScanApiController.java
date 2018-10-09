package io.swagger.api;

import io.swagger.model.ResponseBean;
import io.swagger.model.ScanJSONObject;
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
public class ScanApiController implements ScanApi {

    private static final Logger log = LoggerFactory.getLogger(ScanApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ScanApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResponseBean> addScan(@ApiParam(value = "get projectId and commitId from  JSONObject" ,required=true )  @Valid @RequestBody ScanJSONObject requestParam) {
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

    public ResponseEntity<Void> getCommits(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "project_id", required = true) String projectId,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "category", required = true) String category,@ApiParam(value = "", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") String page,@ApiParam(value = "", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") String size,@ApiParam(value = "", defaultValue = "false") @Valid @RequestParam(value = "is_whole", required = false, defaultValue="false") String isWhole) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
