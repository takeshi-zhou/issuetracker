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
 * CommitTimeData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class CommitTimeData   {
  @JsonProperty("status")
  private String status = null;

  @JsonProperty("commit_time")
  private String commitTime = null;

  public CommitTimeData status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CommitTimeData commitTime(String commitTime) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommitTimeData commitTimeData = (CommitTimeData) o;
    return Objects.equals(this.status, commitTimeData.status) &&
        Objects.equals(this.commitTime, commitTimeData.commitTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, commitTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommitTimeData {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    commitTime: ").append(toIndentedString(commitTime)).append("\n");
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

