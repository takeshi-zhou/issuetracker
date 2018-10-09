/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.ResponseBean;
import io.swagger.model.User;
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

@Api(value = "user", description = "the user API")
public interface UserApi {

    @ApiOperation(value = "Logs user into the system", nickname = "auth", notes = "", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = ResponseBean.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied", response = ResponseBean.class),
        @ApiResponse(code = 404, message = "user not found") })
    @RequestMapping(value = "/user/auth/{userToken}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResponseBean> auth(@ApiParam(value = "token of user that needs to be fetched",required=true) @PathVariable("userToken") String userToken);


    @ApiOperation(value = "Check Account Name", nickname = "checkAccountName", notes = "", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsss", response = ResponseBean.class) })
    @RequestMapping(value = "/user/account-name/check",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResponseBean> checkAccountName(@NotNull @ApiParam(value = "The account name for register", required = true) @Valid @RequestParam(value = "accountName", required = true) String accountName);


    @ApiOperation(value = "Check Email", nickname = "checkEmail", notes = "", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsss", response = ResponseBean.class) })
    @RequestMapping(value = "/user/email/check",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResponseBean> checkEmail(@NotNull @ApiParam(value = "The emailfor register", required = true) @Valid @RequestParam(value = "email", required = true) String email);


    @ApiOperation(value = "Check Nick Name", nickname = "checkNickName", notes = "", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsss", response = ResponseBean.class) })
    @RequestMapping(value = "/user/nick-name/check",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResponseBean> checkNickName(@NotNull @ApiParam(value = "The nickName for register", required = true) @Valid @RequestParam(value = "nickName", required = true) String nickName);


    @ApiOperation(value = "Create user", nickname = "createUser", notes = "This can only be done by the logged in user.", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = ResponseBean.class) })
    @RequestMapping(value = "/user/register",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<ResponseBean> createUser(@ApiParam(value = "Created user object" ,required=true )  @Valid @RequestBody User account);


    @ApiOperation(value = "get AccountID By userToken", nickname = "getAccountID", notes = "", response = String.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = String.class),
        @ApiResponse(code = 400, message = "failed"),
        @ApiResponse(code = 401, message = "Incorrect userToken") })
    @RequestMapping(value = "/user/accountId",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<String> getAccountID(@NotNull @ApiParam(value = "token of user for get accountID", required = true) @Valid @RequestParam(value = "userToken", required = true) String userToken);


    @ApiOperation(value = "get AccountIDs", nickname = "getAccountIds", notes = "", response = String.class, responseContainer = "List", tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "failed") })
    @RequestMapping(value = "/user/accountIds",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<String>> getAccountIds();


    @ApiOperation(value = "Logs user into the system", nickname = "login", notes = "", response = ResponseBean.class, tags={ "account-service", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "succsssful login", response = ResponseBean.class),
        @ApiResponse(code = 400, message = "failed", response = ResponseBean.class),
        @ApiResponse(code = 401, message = "Incorrect username or password", response = ResponseBean.class) })
    @RequestMapping(value = "/user/login",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ResponseBean> login(@NotNull @ApiParam(value = "The user name for login", required = true) @Valid @RequestParam(value = "username", required = true) String username,@NotNull @ApiParam(value = "The password for login in clear text", required = true) @Valid @RequestParam(value = "password", required = true) String password);

}
