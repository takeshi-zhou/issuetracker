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
 * IgnoreTagRequestBody
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IgnoreTagRequestBody   {
  @JsonProperty("ignore-level")
  private String ignoreLevel = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("repo-id")
  private String repoId = null;

  public IgnoreTagRequestBody ignoreLevel(String ignoreLevel) {
    this.ignoreLevel = ignoreLevel;
    return this;
  }

  /**
   * Get ignoreLevel
   * @return ignoreLevel
  **/
  @ApiModelProperty(value = "")


  public String getIgnoreLevel() {
    return ignoreLevel;
  }

  public void setIgnoreLevel(String ignoreLevel) {
    this.ignoreLevel = ignoreLevel;
  }

  public IgnoreTagRequestBody type(String type) {
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

  public IgnoreTagRequestBody repoId(String repoId) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IgnoreTagRequestBody ignoreTagRequestBody = (IgnoreTagRequestBody) o;
    return Objects.equals(this.ignoreLevel, ignoreTagRequestBody.ignoreLevel) &&
        Objects.equals(this.type, ignoreTagRequestBody.type) &&
        Objects.equals(this.repoId, ignoreTagRequestBody.repoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ignoreLevel, type, repoId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IgnoreTagRequestBody {\n");
    
    sb.append("    ignoreLevel: ").append(toIndentedString(ignoreLevel)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    repoId: ").append(toIndentedString(repoId)).append("\n");
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

