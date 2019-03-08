package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.IssueList;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * IssuesMsg
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IssuesMsg   {
  @JsonProperty("start")
  private String start = null;

  @JsonProperty("totalPage")
  private String totalPage = null;

  @JsonProperty("totalCount")
  private String totalCount = null;

  @JsonProperty("issueList")
  @Valid
  private List<IssueList> issueList = null;

  public IssuesMsg start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
  **/
  @ApiModelProperty(value = "")


  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public IssuesMsg totalPage(String totalPage) {
    this.totalPage = totalPage;
    return this;
  }

  /**
   * Get totalPage
   * @return totalPage
  **/
  @ApiModelProperty(value = "")


  public String getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(String totalPage) {
    this.totalPage = totalPage;
  }

  public IssuesMsg totalCount(String totalCount) {
    this.totalCount = totalCount;
    return this;
  }

  /**
   * Get totalCount
   * @return totalCount
  **/
  @ApiModelProperty(value = "")


  public String getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(String totalCount) {
    this.totalCount = totalCount;
  }

  public IssuesMsg issueList(List<IssueList> issueList) {
    this.issueList = issueList;
    return this;
  }

  public IssuesMsg addIssueListItem(IssueList issueListItem) {
    if (this.issueList == null) {
      this.issueList = new ArrayList<IssueList>();
    }
    this.issueList.add(issueListItem);
    return this;
  }

  /**
   * Get issueList
   * @return issueList
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<IssueList> getIssueList() {
    return issueList;
  }

  public void setIssueList(List<IssueList> issueList) {
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
    IssuesMsg issuesMsg = (IssuesMsg) o;
    return Objects.equals(this.start, issuesMsg.start) &&
        Objects.equals(this.totalPage, issuesMsg.totalPage) &&
        Objects.equals(this.totalCount, issuesMsg.totalCount) &&
        Objects.equals(this.issueList, issuesMsg.issueList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, totalPage, totalCount, issueList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IssuesMsg {\n");
    
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
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

