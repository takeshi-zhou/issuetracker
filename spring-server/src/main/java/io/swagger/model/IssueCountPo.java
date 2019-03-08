package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.threeten.bp.LocalDate;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * IssueCountPo
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class IssueCountPo   {
  @JsonProperty("date")
  private LocalDate date = null;

  @JsonProperty("newIssueCount")
  private Integer newIssueCount = null;

  @JsonProperty("eliminatedIssueCount")
  private Integer eliminatedIssueCount = null;

  @JsonProperty("remainingIssueCount")
  private Integer remainingIssueCount = null;

  public IssueCountPo date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
  **/
  @ApiModelProperty(value = "")

  @Valid

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public IssueCountPo newIssueCount(Integer newIssueCount) {
    this.newIssueCount = newIssueCount;
    return this;
  }

  /**
   * Get newIssueCount
   * @return newIssueCount
  **/
  @ApiModelProperty(value = "")


  public Integer getNewIssueCount() {
    return newIssueCount;
  }

  public void setNewIssueCount(Integer newIssueCount) {
    this.newIssueCount = newIssueCount;
  }

  public IssueCountPo eliminatedIssueCount(Integer eliminatedIssueCount) {
    this.eliminatedIssueCount = eliminatedIssueCount;
    return this;
  }

  /**
   * Get eliminatedIssueCount
   * @return eliminatedIssueCount
  **/
  @ApiModelProperty(value = "")


  public Integer getEliminatedIssueCount() {
    return eliminatedIssueCount;
  }

  public void setEliminatedIssueCount(Integer eliminatedIssueCount) {
    this.eliminatedIssueCount = eliminatedIssueCount;
  }

  public IssueCountPo remainingIssueCount(Integer remainingIssueCount) {
    this.remainingIssueCount = remainingIssueCount;
    return this;
  }

  /**
   * Get remainingIssueCount
   * @return remainingIssueCount
  **/
  @ApiModelProperty(value = "")


  public Integer getRemainingIssueCount() {
    return remainingIssueCount;
  }

  public void setRemainingIssueCount(Integer remainingIssueCount) {
    this.remainingIssueCount = remainingIssueCount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IssueCountPo issueCountPo = (IssueCountPo) o;
    return Objects.equals(this.date, issueCountPo.date) &&
        Objects.equals(this.newIssueCount, issueCountPo.newIssueCount) &&
        Objects.equals(this.eliminatedIssueCount, issueCountPo.eliminatedIssueCount) &&
        Objects.equals(this.remainingIssueCount, issueCountPo.remainingIssueCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, newIssueCount, eliminatedIssueCount, remainingIssueCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IssueCountPo {\n");
    
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    newIssueCount: ").append(toIndentedString(newIssueCount)).append("\n");
    sb.append("    eliminatedIssueCount: ").append(toIndentedString(eliminatedIssueCount)).append("\n");
    sb.append("    remainingIssueCount: ").append(toIndentedString(remainingIssueCount)).append("\n");
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

