package io.swagger.api;

import io.swagger.model.ResponseBean;
import io.swagger.model.User;
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
public class UserApiController implements UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResponseBean> auth(@ApiParam(value = "token of user that needs to be fetched",required=true) @PathVariable("userToken") String userToken) {
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

    public ResponseEntity<ResponseBean> checkAccountName(@NotNull @ApiParam(value = "The account name for register", required = true) @Valid @RequestParam(value = "accountName", required = true) String accountName) {
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

    public ResponseEntity<ResponseBean> checkEmail(@NotNull @ApiParam(value = "The emailfor register", required = true) @Valid @RequestParam(value = "email", required = true) String email) {
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

    public ResponseEntity<ResponseBean> checkNickName(@NotNull @ApiParam(value = "The nickName for register", required = true) @Valid @RequestParam(value = "nickName", required = true) String nickName) {
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

    public ResponseEntity<ResponseBean> createUser(@ApiParam(value = "Created user object" ,required=true )  @Valid @RequestBody User account) {
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

    public ResponseEntity<String> getAccountID(@NotNull @ApiParam(value = "token of user for get accountID", required = true) @Valid @RequestParam(value = "userToken", required = true) String userToken) {
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

    public ResponseEntity<List<String>> getAccountIds() {
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

    public ResponseEntity<ResponseBean> login(@NotNull @ApiParam(value = "The user name for login", required = true) @Valid @RequestParam(value = "username", required = true) String username,@NotNull @ApiParam(value = "The password for login in clear text", required = true) @Valid @RequestParam(value = "password", required = true) String password) {
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
