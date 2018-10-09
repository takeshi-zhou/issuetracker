package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.CommitsMsgCommitList;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CommitsMsg
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class CommitsMsg   {
  @JsonProperty("totalCount")
  private Integer totalCount = null;

  @JsonProperty("commitList")
  @Valid
  private List<CommitsMsgCommitList> commitList = null;

  public CommitsMsg totalCount(Integer totalCount) {
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

  public CommitsMsg commitList(List<CommitsMsgCommitList> commitList) {
    this.commitList = commitList;
    return this;
  }

  public CommitsMsg addCommitListItem(CommitsMsgCommitList commitListItem) {
    if (this.commitList == null) {
      this.commitList = new ArrayList<CommitsMsgCommitList>();
    }
    this.commitList.add(commitListItem);
    return this;
  }

  /**
   * Get commitList
   * @return commitList
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<CommitsMsgCommitList> getCommitList() {
    return commitList;
  }

  public void setCommitList(List<CommitsMsgCommitList> commitList) {
    this.commitList = commitList;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommitsMsg commitsMsg = (CommitsMsg) o;
    return Objects.equals(this.totalCount, commitsMsg.totalCount) &&
        Objects.equals(this.commitList, commitsMsg.commitList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalCount, commitList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommitsMsg {\n");
    
    sb.append("    totalCount: ").append(toIndentedString(totalCount)).append("\n");
    sb.append("    commitList: ").append(toIndentedString(commitList)).append("\n");
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

