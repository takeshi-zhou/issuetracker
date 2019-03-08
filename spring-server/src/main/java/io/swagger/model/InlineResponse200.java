package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.IssueList;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * InlineResponse200
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class InlineResponse200   {
  @JsonProperty("totalPage")
  private Integer totalPage = null;

  @JsonProperty("totalCount")
  private Integer totalCount = null;

  @JsonProperty("issueList")
  private IssueList issueList = null;

  public InlineResponse200 totalPage(Integer totalPage) {
    this.totalPage = totalPage;
    return this;
  }

  /**
   * Get totalPage
   * @return totalPage
  **/
  @ApiModelProperty(value = "")


  public Integer getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(Integer totalPage) {
    this.totalPage = totalPage;
  }

  public InlineResponse200 totalCount(Integer totalCount) {
    this.totalCount = totalCount;
    return this;
  }

  /**
   * Get totalCount
   * @return totalCount
  **/
  @ApiModelProperty(value = "")


  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public InlineResponse200 issueList(IssueList issueList) {
    this.issueList = issueList;
    return this;
  }

  /**
   * Get issueList
   * @return issueList
  **/
  @ApiModelProperty(value = "")

  @Valid

  public IssueList getIssueList() {
    return issueList;
  }

  public void setIssueList(IssueList issueList) {
    this.issueList = issueList;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse200 inlineResponse200 = (InlineResponse200) o;
    return Objects.equals(this.totalPage, inlineResponse200.totalPage) &&
        Objects.equals(this.totalCount, inlineResponse200.totalCount) &&
        Objects.equals(this.issueList, inlineResponse200.issueList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPage, totalCount, issueList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse200 {\n");
    
    sb.append("    totalPage: ").append(toIndentedString(totalPage)).append("\n");
    sb.append("    totalCount: ").append(toIndentedString(totalCount)).append("\n");
    sb.append("    issueList: ").append(toIndentedString(issueList)).append("\n");
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

