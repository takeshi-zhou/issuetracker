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
 * IssueParam
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IssueParam   {
  @JsonProperty("projectId")
  private String projectId = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("duration")
  private String duration = null;

  @JsonProperty("page")
  private Integer page = null;

  @JsonProperty("size")
  private Integer size = null;

  @JsonProperty("onlyNew")
  private Boolean onlyNew = null;

  @JsonProperty("onlyEliminated")
  private Boolean onlyEliminated = null;

  @JsonProperty("types")
  @Valid
  private List<String> types = null;

  @JsonProperty("tags")
  @Valid
  private List<String> tags = null;

  public IssueParam projectId(String projectId) {
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

  public IssueParam category(String category) {
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

  public IssueParam duration(String duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Get duration
   * @return duration
  **/
  @ApiModelProperty(value = "")


  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public IssueParam page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
  **/
  @ApiModelProperty(value = "")


  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public IssueParam size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * @return size
  **/
  @ApiModelProperty(value = "")


  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public IssueParam onlyNew(Boolean onlyNew) {
    this.onlyNew = onlyNew;
    return this;
  }

  /**
   * Get onlyNew
   * @return onlyNew
  **/
  @ApiModelProperty(value = "")


  public Boolean isOnlyNew() {
    return onlyNew;
  }

  public void setOnlyNew(Boolean onlyNew) {
    this.onlyNew = onlyNew;
  }

  public IssueParam onlyEliminated(Boolean onlyEliminated) {
    this.onlyEliminated = onlyEliminated;
    return this;
  }

  /**
   * Get onlyEliminated
   * @return onlyEliminated
  **/
  @ApiModelProperty(value = "")


  public Boolean isOnlyEliminated() {
    return onlyEliminated;
  }

  public void setOnlyEliminated(Boolean onlyEliminated) {
    this.onlyEliminated = onlyEliminated;
  }

  public IssueParam types(List<String> types) {
    this.types = types;
    return this;
  }

  public IssueParam addTypesItem(String typesItem) {
    if (this.types == null) {
      this.types = new ArrayList<String>();
    }
    this.types.add(typesItem);
    return this;
  }

  /**
   * Get types
   * @return types
  **/
  @ApiModelProperty(value = "")


  public List<String> getTypes() {
    return types;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }

  public IssueParam tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public IssueParam addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<String>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   * @return tags
  **/
  @ApiModelProperty(value = "")


  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
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
    IssueParam issueParam = (IssueParam) o;
    return Objects.equals(this.projectId, issueParam.projectId) &&
        Objects.equals(this.category, issueParam.category) &&
        Objects.equals(this.duration, issueParam.duration) &&
        Objects.equals(this.page, issueParam.page) &&
        Objects.equals(this.size, issueParam.size) &&
        Objects.equals(this.onlyNew, issueParam.onlyNew) &&
        Objects.equals(this.onlyEliminated, issueParam.onlyEliminated) &&
        Objects.equals(this.types, issueParam.types) &&
        Objects.equals(this.tags, issueParam.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, category, duration, page, size, onlyNew, onlyEliminated, types, tags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IssueParam {\n");
    
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    onlyNew: ").append(toIndentedString(onlyNew)).append("\n");
    sb.append("    onlyEliminated: ").append(toIndentedString(onlyEliminated)).append("\n");
    sb.append("    types: ").append(toIndentedString(types)).append("\n");
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

