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
 * RawIssue
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class RawIssue   {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("detail")
  private String detail = null;

  @JsonProperty("file_name")
  private String fileName = null;

  @JsonProperty("scan_id")
  private String scanId = null;

  @JsonProperty("issue_id")
  private String issueId = null;

  @JsonProperty("commit_id")
  private String commitId = null;

  @JsonProperty("commit_time")
  private String commitTime = null;

  @JsonProperty("locations")
  @Valid
  private List<Object> locations = null;

  public RawIssue id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RawIssue uuid(String uuid) {
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

  public RawIssue type(String type) {
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

  public RawIssue detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Get detail
   * @return detail
  **/
  @ApiModelProperty(value = "")


  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public RawIssue fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
  **/
  @ApiModelProperty(value = "")


  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public RawIssue scanId(String scanId) {
    this.scanId = scanId;
    return this;
  }

  /**
   * Get scanId
   * @return scanId
  **/
  @ApiModelProperty(value = "")


  public String getScanId() {
    return scanId;
  }

  public void setScanId(String scanId) {
    this.scanId = scanId;
  }

  public RawIssue issueId(String issueId) {
    this.issueId = issueId;
    return this;
  }

  /**
   * Get issueId
   * @return issueId
  **/
  @ApiModelProperty(value = "")


  public String getIssueId() {
    return issueId;
  }

  public void setIssueId(String issueId) {
    this.issueId = issueId;
  }

  public RawIssue commitId(String commitId) {
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

  public RawIssue commitTime(String commitTime) {
    this.commitTime = commitTime;
    return this;
  }

  /**
   * Get commitTime
   * @return commitTime
  **/
  @ApiModelProperty(value = "")


  public String getCommitTime() {
    return commitTime;
  }

  public void setCommitTime(String commitTime) {
    this.commitTime = commitTime;
  }

  public RawIssue locations(List<Object> locations) {
    this.locations = locations;
    return this;
  }

  public RawIssue addLocationsItem(Object locationsItem) {
    if (this.locations == null) {
      this.locations = new ArrayList<Object>();
    }
    this.locations.add(locationsItem);
    return this;
  }

  /**
   * Get locations
   * @return locations
  **/
  @ApiModelProperty(value = "")


  public List<Object> getLocations() {
    return locations;
  }

  public void setLocations(List<Object> locations) {
    this.locations = locations;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RawIssue rawIssue = (RawIssue) o;
    return Objects.equals(this.id, rawIssue.id) &&
        Objects.equals(this.uuid, rawIssue.uuid) &&
        Objects.equals(this.type, rawIssue.type) &&
        Objects.equals(this.detail, rawIssue.detail) &&
        Objects.equals(this.fileName, rawIssue.fileName) &&
        Objects.equals(this.scanId, rawIssue.scanId) &&
        Objects.equals(this.issueId, rawIssue.issueId) &&
        Objects.equals(this.commitId, rawIssue.commitId) &&
        Objects.equals(this.commitTime, rawIssue.commitTime) &&
        Objects.equals(this.locations, rawIssue.locations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uuid, type, detail, fileName, scanId, issueId, commitId, commitTime, locations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RawIssue {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    scanId: ").append(toIndentedString(scanId)).append("\n");
    sb.append("    issueId: ").append(toIndentedString(issueId)).append("\n");
    sb.append("    commitId: ").append(toIndentedString(commitId)).append("\n");
    sb.append("    commitTime: ").append(toIndentedString(commitTime)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
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

