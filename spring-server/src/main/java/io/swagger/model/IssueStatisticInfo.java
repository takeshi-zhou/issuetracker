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
 * IssueStatisticInfo
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IssueStatisticInfo   {
  @JsonProperty("avgEliminatedTime")
  private Double avgEliminatedTime = null;

  @JsonProperty("maxAliveTime")
  private Long maxAliveTime = null;

  public IssueStatisticInfo avgEliminatedTime(Double avgEliminatedTime) {
    this.avgEliminatedTime = avgEliminatedTime;
    return this;
  }

  /**
   * Get avgEliminatedTime
   * @return avgEliminatedTime
  **/
  @ApiModelProperty(value = "")


  public Double getAvgEliminatedTime() {
    return avgEliminatedTime;
  }

  public void setAvgEliminatedTime(Double avgEliminatedTime) {
    this.avgEliminatedTime = avgEliminatedTime;
  }

  public IssueStatisticInfo maxAliveTime(Long maxAliveTime) {
    this.maxAliveTime = maxAliveTime;
    return this;
  }

  /**
   * Get maxAliveTime
   * @return maxAliveTime
  **/
  @ApiModelProperty(value = "")


  public Long getMaxAliveTime() {
    return maxAliveTime;
  }

  public void setMaxAliveTime(Long maxAliveTime) {
    this.maxAliveTime = maxAliveTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IssueStatisticInfo issueStatisticInfo = (IssueStatisticInfo) o;
    return Objects.equals(this.avgEliminatedTime, issueStatisticInfo.avgEliminatedTime) &&
        Objects.equals(this.maxAliveTime, issueStatisticInfo.maxAliveTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(avgEliminatedTime, maxAliveTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IssueStatisticInfo {\n");
    
    sb.append("    avgEliminatedTime: ").append(toIndentedString(avgEliminatedTime)).append("\n");
    sb.append("    maxAliveTime: ").append(toIndentedString(maxAliveTime)).append("\n");
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

