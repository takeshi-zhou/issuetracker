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
 * Scan
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class Scan   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("start_time")
  private String startTime = null;

  @JsonProperty("end_time")
  private String endTime = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("result_summary")
  private String resultSummary = null;

  @JsonProperty("project_id")
  private String projectId = null;

  @JsonProperty("commit_id")
  private String commitId = null;

  public Scan uuid(String uuid) {
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

  public Scan name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Scan startTime(String startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * Get startTime
   * @return startTime
  **/
  @ApiModelProperty(value = "")


  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public Scan endTime(String endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * Get endTime
   * @return endTime
  **/
  @ApiModelProperty(value = "")


  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Scan status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Scan resultSummary(String resultSummary) {
    this.resultSummary = resultSummary;
    return this;
  }

  /**
   * Get resultSummary
   * @return resultSummary
  **/
  @ApiModelProperty(value = "")


  public String getResultSummary() {
    return resultSummary;
  }

  public void setResultSummary(String resultSummary) {
    this.resultSummary = resultSummary;
  }

  public Scan projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * Get projectId
   * @return projectId
  **/
  @ApiModelProperty(value = "")


  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public Scan commitId(String commitId) {
    this.commitId = commitId;
    return this;
  }

  /**
   * Get commitId
   * @return commitId
  **/
  @ApiModelProperty(value = "")


  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Scan scan = (Scan) o;
    return Objects.equals(this.uuid, scan.uuid) &&
        Objects.equals(this.name, scan.name) &&
        Objects.equals(this.startTime, scan.startTime) &&
        Objects.equals(this.endTime, scan.endTime) &&
        Objects.equals(this.status, scan.status) &&
        Objects.equals(this.resultSummary, scan.resultSummary) &&
        Objects.equals(this.projectId, scan.projectId) &&
        Objects.equals(this.commitId, scan.commitId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, startTime, endTime, status, resultSummary, projectId, commitId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Scan {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    resultSummary: ").append(toIndentedString(resultSummary)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    commitId: ").append(toIndentedString(commitId)).append("\n");
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

