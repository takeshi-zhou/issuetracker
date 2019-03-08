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
 * ScanRequestParam
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class ScanRequestParam   {
  @JsonProperty("category")
  private String category = null;

  @JsonProperty("projectId")
  private String projectId = null;

  @JsonProperty("commitId")
  private String commitId = null;

  public ScanRequestParam category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  **/
  @ApiModelProperty(value = "")


  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ScanRequestParam projectId(String projectId) {
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

  public ScanRequestParam commitId(String commitId) {
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
    ScanRequestParam scanRequestParam = (ScanRequestParam) o;
    return Objects.equals(this.category, scanRequestParam.category) &&
        Objects.equals(this.projectId, scanRequestParam.projectId) &&
        Objects.equals(this.commitId, scanRequestParam.commitId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, projectId, commitId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ScanRequestParam {\n");
    
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

