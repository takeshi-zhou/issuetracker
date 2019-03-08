package io.swagger.api;

import io.swagger.model.AddTagRequestBody;
import io.swagger.model.IgnoreRecord;
import io.swagger.model.IgnoreTagRequestBody;
import io.swagger.model.ModifyTagRequestBody;
import io.swagger.model.ResponseBean;
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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

@Controller
public class TagsApiController implements TagsApi {

    private static final Logger log = LoggerFactory.getLogger(TagsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TagsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResponseBean> addTag(@ApiParam(value = " " ,required=true )  @Valid @RequestBody AddTagRequestBody addTagRequestBody) {
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

    public ResponseEntity<ResponseBean> cancelIgnoreRecord(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "repo-id", required = true) String repoId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "level", required = true) String level,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "type", required = true) String type,@ApiParam(value = " " ,required=true) @RequestHeader(value="token", required=true) String token) {
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

    public ResponseEntity<ResponseBean> deleteTag(@ApiParam(value = " ",required=true) @PathVariable("tag-id") String tagId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "item-id", required = true) String itemId) {
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

    public ResponseEntity<TagList> getAllDefaultTags() {
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

    public ResponseEntity<List<IgnoreRecord>> getIgnoreRecordList(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "repo-id", required = true) String repoId,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "level", required = true) String level,@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "type", required = true) String type,@ApiParam(value = " " ,required=true) @RequestHeader(value="token", required=true) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<IgnoreRecord>>(objectMapper.readValue("[ {  \"repoId\" : \"repoId\",  \"level\" : 0,  \"repoName\" : \"repoName\",  \"type\" : \"type\",  \"uuid\" : \"uuid\",  \"userId\" : \"userId\"}, {  \"repoId\" : \"repoId\",  \"level\" : 0,  \"repoName\" : \"repoName\",  \"type\" : \"type\",  \"uuid\" : \"uuid\",  \"userId\" : \"userId\"} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<IgnoreRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<IgnoreRecord>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ResponseBean> ignoreOneType(@ApiParam(value = " " ,required=true )  @Valid @RequestBody IgnoreTagRequestBody ignoreTagRequestBody,@ApiParam(value = " " ,required=true) @RequestHeader(value="token", required=true) String token) {
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

    public ResponseEntity<ResponseBean> isSolved(@NotNull @ApiParam(value = " ", required = true) @Valid @RequestParam(value = "issueId", required = true) String issueId) {
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

    public ResponseEntity<ResponseBean> modifyTag(@ApiParam(value = " " ,required=true )  @Valid @RequestBody ModifyTagRequestBody modifyTagRequestBody) {
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
