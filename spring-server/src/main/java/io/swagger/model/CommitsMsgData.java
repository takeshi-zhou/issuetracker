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
 * CommitsMsgData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class CommitsMsgData   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("commit_id")
  private String commitId = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("developer")
  private String developer = null;

  @JsonProperty("commit_time")
  private String commitTime = null;

  @JsonProperty("repo_id")
  private String repoId = null;

  @JsonProperty("is_scanned")
  private Boolean isScanned = null;

  public CommitsMsgData uuid(String uuid) {
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

  public CommitsMsgData commitId(String commitId) {
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

  public CommitsMsgData message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(value = "")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public CommitsMsgData developer(String developer) {
    this.developer = developer;
    return this;
  }

  /**
   * Get developer
   * @return developer
  **/
  @ApiModelProperty(value = "")


  public String getDeveloper() {
    return developer;
  }

  public void setDeveloper(String developer) {
    this.developer = developer;
  }

  public CommitsMsgData commitTime(String commitTime) {
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

  public CommitsMsgData repoId(String repoId) {
    this.repoId = repoId;
    return this;
  }

  /**
   * Get repoId
   * @return repoId
  **/
  @ApiModelProperty(value = "")


  public String getRepoId() {
    return repoId;
  }

  public void setRepoId(String repoId) {
    this.repoId = repoId;
  }

  public CommitsMsgData isScanned(Boolean isScanned) {
    this.isScanned = isScanned;
    return this;
  }

  /**
   * Get isScanned
   * @return isScanned
  **/
  @ApiModelProperty(value = "")


  public Boolean isIsScanned() {
    return isScanned;
  }

  public void setIsScanned(Boolean isScanned) {
    this.isScanned = isScanned;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommitsMsgData commitsMsgData = (CommitsMsgData) o;
    return Objects.equals(this.uuid, commitsMsgData.uuid) &&
        Objects.equals(this.commitId, commitsMsgData.commitId) &&
        Objects.equals(this.message, commitsMsgData.message) &&
        Objects.equals(this.developer, commitsMsgData.developer) &&
        Objects.equals(this.commitTime, commitsMsgData.commitTime) &&
        Objects.equals(this.repoId, commitsMsgData.repoId) &&
        Objects.equals(this.isScanned, commitsMsgData.isScanned);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, commitId, message, developer, commitTime, repoId, isScanned);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommitsMsgData {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    commitId: ").append(toIndentedString(commitId)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    developer: ").append(toIndentedString(developer)).append("\n");
    sb.append("    commitTime: ").append(toIndentedString(commitTime)).append("\n");
    sb.append("    repoId: ").append(toIndentedString(repoId)).append("\n");
    sb.append("    isScanned: ").append(toIndentedString(isScanned)).append("\n");
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

