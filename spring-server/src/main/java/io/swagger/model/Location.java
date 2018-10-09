package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Location
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class Location   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("start_line")
  private Integer startLine = null;

  @JsonProperty("end_line")
  private Integer endLine = null;

  @JsonProperty("bug_lines")
  private String bugLines = null;

  @JsonProperty("file_path")
  private String filePath = null;

  @JsonProperty("class_name")
  private String className = null;

  @JsonProperty("method_name")
  private String methodName = null;

  @JsonProperty("rawIssue_id")
  private String rawIssueId = null;

  @JsonProperty("code")
  private String code = null;

  public Location uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * Get uuid
   * @return uuid
  **/
  @ApiModelProperty(value = "")


  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Location startLine(Integer startLine) {
    this.startLine = startLine;
    return this;
  }

  /**
   * Get startLine
   * @return startLine
  **/
  @ApiModelProperty(value = "")


  public Integer getStartLine() {
    return startLine;
  }

  public void setStartLine(Integer startLine) {
    this.startLine = startLine;
  }

  public Location endLine(Integer endLine) {
    this.endLine = endLine;
    return this;
  }

  /**
   * Get endLine
   * @return endLine
  **/
  @ApiModelProperty(value = "")


  public Integer getEndLine() {
    return endLine;
  }

  public void setEndLine(Integer endLine) {
    this.endLine = endLine;
  }

  public Location bugLines(String bugLines) {
    this.bugLines = bugLines;
    return this;
  }

  /**
   * Get bugLines
   * @return bugLines
  **/
  @ApiModelProperty(value = "")


  public String getBugLines() {
    return bugLines;
  }

  public void setBugLines(String bugLines) {
    this.bugLines = bugLines;
  }

  public Location filePath(String filePath) {
    this.filePath = filePath;
    return this;
  }

  /**
   * Get filePath
   * @return filePath
  **/
  @ApiModelProperty(value = "")


  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Location className(String className) {
    this.className = className;
    return this;
  }

  /**
   * Get className
   * @return className
  **/
  @ApiModelProperty(value = "")


  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Location methodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  /**
   * Get methodName
   * @return methodName
  **/
  @ApiModelProperty(value = "")


  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Location rawIssueId(String rawIssueId) {
    this.rawIssueId = rawIssueId;
    return this;
  }

  /**
   * Get rawIssueId
   * @return rawIssueId
  **/
  @ApiModelProperty(value = "")


  public String getRawIssueId() {
    return rawIssueId;
  }

  public void setRawIssueId(String rawIssueId) {
    this.rawIssueId = rawIssueId;
  }

  public Location code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(value = "")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return Objects.equals(this.uuid, location.uuid) &&
        Objects.equals(this.startLine, location.startLine) &&
        Objects.equals(this.endLine, location.endLine) &&
        Objects.equals(this.bugLines, location.bugLines) &&
        Objects.equals(this.filePath, location.filePath) &&
        Objects.equals(this.className, location.className) &&
        Objects.equals(this.methodName, location.methodName) &&
        Objects.equals(this.rawIssueId, location.rawIssueId) &&
        Objects.equals(this.code, location.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, startLine, endLine, bugLines, filePath, className, methodName, rawIssueId, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Location {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    startLine: ").append(toIndentedString(startLine)).append("\n");
    sb.append("    endLine: ").append(toIndentedString(endLine)).append("\n");
    sb.append("    bugLines: ").append(toIndentedString(bugLines)).append("\n");
    sb.append("    filePath: ").append(toIndentedString(filePath)).append("\n");
    sb.append("    className: ").append(toIndentedString(className)).append("\n");
    sb.append("    methodName: ").append(toIndentedString(methodName)).append("\n");
    sb.append("    rawIssueId: ").append(toIndentedString(rawIssueId)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

