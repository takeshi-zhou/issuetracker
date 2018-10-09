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
 * InlineResponse2001
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class InlineResponse2001   {
  @JsonProperty("newIssueCount")
  private Integer newIssueCount = null;

  @JsonProperty("eliminatedIssueCount")
  private Integer eliminatedIssueCount = null;

  @JsonProperty("remainingIssueCount")
  private Integer remainingIssueCount = null;

  public InlineResponse2001 newIssueCount(Integer newIssueCount) {
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

  public InlineResponse2001 eliminatedIssueCount(Integer eliminatedIssueCount) {
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

  public InlineResponse2001 remainingIssueCount(Integer remainingIssueCount) {
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
    InlineResponse2001 inlineResponse2001 = (InlineResponse2001) o;
    return Objects.equals(this.newIssueCount, inlineResponse2001.newIssueCount) &&
        Objects.equals(this.eliminatedIssueCount, inlineResponse2001.eliminatedIssueCount) &&
        Objects.equals(this.remainingIssueCount, inlineResponse2001.remainingIssueCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(newIssueCount, eliminatedIssueCount, remainingIssueCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2001 {\n");
    
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

