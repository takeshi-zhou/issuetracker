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
 * IssueJSONObject
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class IssueJSONObject   {
  @JsonProperty("repo_id")
  private String repoId = null;

  @JsonProperty("pre_commit_id")
  private String preCommitId = null;

  @JsonProperty("current_commit_id")
  private String currentCommitId = null;

  @JsonProperty("category")
  private String category = null;

  public IssueJSONObject repoId(String repoId) {
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

  public IssueJSONObject preCommitId(String preCommitId) {
    this.preCommitId = preCommitId;
    return this;
  }

  /**
   * Get preCommitId
   * @return preCommitId
  **/
  @ApiModelProperty(value = "")


  public String getPreCommitId() {
    return preCommitId;
  }

  public void setPreCommitId(String preCommitId) {
    this.preCommitId = preCommitId;
  }

  public IssueJSONObject currentCommitId(String currentCommitId) {
    this.currentCommitId = currentCommitId;
    return this;
  }

  /**
   * Get currentCommitId
   * @return currentCommitId
  **/
  @ApiModelProperty(value = "")


  public String getCurrentCommitId() {
    return currentCommitId;
  }

  public void setCurrentCommitId(String currentCommitId) {
    this.currentCommitId = currentCommitId;
  }

  public IssueJSONObject category(String category) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IssueJSONObject issueJSONObject = (IssueJSONObject) o;
    return Objects.equals(this.repoId, issueJSONObject.repoId) &&
        Objects.equals(this.preCommitId, issueJSONObject.preCommitId) &&
        Objects.equals(this.currentCommitId, issueJSONObject.currentCommitId) &&
        Objects.equals(this.category, issueJSONObject.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repoId, preCommitId, currentCommitId, category);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IssueJSONObject {\n");
    
    sb.append("    repoId: ").append(toIndentedString(repoId)).append("\n");
    sb.append("    preCommitId: ").append(toIndentedString(preCommitId)).append("\n");
    sb.append("    currentCommitId: ").append(toIndentedString(currentCommitId)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

