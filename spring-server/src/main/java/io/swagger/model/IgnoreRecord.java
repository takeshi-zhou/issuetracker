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
 * IgnoreRecord
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IgnoreRecord   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("userId")
  private String userId = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("repoId")
  private String repoId = null;

  @JsonProperty("level")
  private Integer level = null;

  @JsonProperty("repoName")
  private String repoName = null;

  public IgnoreRecord uuid(String uuid) {
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

  public IgnoreRecord userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  **/
  @ApiModelProperty(value = "")


  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public IgnoreRecord type(String type) {
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

  public IgnoreRecord repoId(String repoId) {
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

  public IgnoreRecord level(Integer level) {
    this.level = level;
    return this;
  }

  /**
   * Get level
   * @return level
  **/
  @ApiModelProperty(value = "")


  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public IgnoreRecord repoName(String repoName) {
    this.repoName = repoName;
    return this;
  }

  /**
   * Get repoName
   * @return repoName
  **/
  @ApiModelProperty(value = "")


  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IgnoreRecord ignoreRecord = (IgnoreRecord) o;
    return Objects.equals(this.uuid, ignoreRecord.uuid) &&
        Objects.equals(this.userId, ignoreRecord.userId) &&
        Objects.equals(this.type, ignoreRecord.type) &&
        Objects.equals(this.repoId, ignoreRecord.repoId) &&
        Objects.equals(this.level, ignoreRecord.level) &&
        Objects.equals(this.repoName, ignoreRecord.repoName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, userId, type, repoId, level, repoName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IgnoreRecord {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    repoId: ").append(toIndentedString(repoId)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("    repoName: ").append(toIndentedString(repoName)).append("\n");
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

