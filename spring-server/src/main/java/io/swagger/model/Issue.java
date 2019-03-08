package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Issue
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class Issue   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("start_commit")
  private String startCommit = null;

  @JsonProperty("end_commit")
  private String endCommit = null;

  @JsonProperty("raw_issue_start")
  private String rawIssueStart = null;

  @JsonProperty("raw_issue_end")
  private String rawIssueEnd = null;

  @JsonProperty("project_id")
  private String projectId = null;

  @JsonProperty("target_files")
  private String targetFiles = null;

  @JsonProperty("issueType")
  private Object issueType = null;

  @JsonProperty("tags")
  @Valid
  private List<Object> tags = null;

  public Issue uuid(String uuid) {
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

  public Issue type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Issue startCommit(String startCommit) {
    this.startCommit = startCommit;
    return this;
  }

  /**
   * Get startCommit
   * @return startCommit
  **/
  @ApiModelProperty(value = "")


  public String getStartCommit() {
    return startCommit;
  }

  public void setStartCommit(String startCommit) {
    this.startCommit = startCommit;
  }

  public Issue endCommit(String endCommit) {
    this.endCommit = endCommit;
    return this;
  }

  /**
   * Get endCommit
   * @return endCommit
  **/
  @ApiModelProperty(value = "")


  public String getEndCommit() {
    return endCommit;
  }

  public void setEndCommit(String endCommit) {
    this.endCommit = endCommit;
  }

  public Issue rawIssueStart(String rawIssueStart) {
    this.rawIssueStart = rawIssueStart;
    return this;
  }

  /**
   * Get rawIssueStart
   * @return rawIssueStart
  **/
  @ApiModelProperty(value = "")


  public String getRawIssueStart() {
    return rawIssueStart;
  }

  public void setRawIssueStart(String rawIssueStart) {
    this.rawIssueStart = rawIssueStart;
  }

  public Issue rawIssueEnd(String rawIssueEnd) {
    this.rawIssueEnd = rawIssueEnd;
    return this;
  }

  /**
   * Get rawIssueEnd
   * @return rawIssueEnd
  **/
  @ApiModelProperty(value = "")


  public String getRawIssueEnd() {
    return rawIssueEnd;
  }

  public void setRawIssueEnd(String rawIssueEnd) {
    this.rawIssueEnd = rawIssueEnd;
  }

  public Issue projectId(String projectId) {
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

  public Issue targetFiles(String targetFiles) {
    this.targetFiles = targetFiles;
    return this;
  }

  /**
   * Get targetFiles
   * @return targetFiles
  **/
  @ApiModelProperty(value = "")


  public String getTargetFiles() {
    return targetFiles;
  }

  public void setTargetFiles(String targetFiles) {
    this.targetFiles = targetFiles;
  }

  public Issue issueType(Object issueType) {
    this.issueType = issueType;
    return this;
  }

  /**
   * Get issueType
   * @return issueType
  **/
  @ApiModelProperty(value = "")


  public Object getIssueType() {
    return issueType;
  }

  public void setIssueType(Object issueType) {
    this.issueType = issueType;
  }

  public Issue tags(List<Object> tags) {
    this.tags = tags;
    return this;
  }

  public Issue addTagsItem(Object tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<Object>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   * @return tags
  **/
  @ApiModelProperty(value = "")


  public List<Object> getTags() {
    return tags;
  }

  public void setTags(List<Object> tags) {
    this.tags = tags;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Issue issue = (Issue) o;
    return Objects.equals(this.uuid, issue.uuid) &&
        Objects.equals(this.type, issue.type) &&
        Objects.equals(this.startCommit, issue.startCommit) &&
        Objects.equals(this.endCommit, issue.endCommit) &&
        Objects.equals(this.rawIssueStart, issue.rawIssueStart) &&
        Objects.equals(this.rawIssueEnd, issue.rawIssueEnd) &&
        Objects.equals(this.projectId, issue.projectId) &&
        Objects.equals(this.targetFiles, issue.targetFiles) &&
        Objects.equals(this.issueType, issue.issueType) &&
        Objects.equals(this.tags, issue.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, type, startCommit, endCommit, rawIssueStart, rawIssueEnd, projectId, targetFiles, issueType, tags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Issue {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    startCommit: ").append(toIndentedString(startCommit)).append("\n");
    sb.append("    endCommit: ").append(toIndentedString(endCommit)).append("\n");
    sb.append("    rawIssueStart: ").append(toIndentedString(rawIssueStart)).append("\n");
    sb.append("    rawIssueEnd: ").append(toIndentedString(rawIssueEnd)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    targetFiles: ").append(toIndentedString(targetFiles)).append("\n");
    sb.append("    issueType: ").append(toIndentedString(issueType)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

