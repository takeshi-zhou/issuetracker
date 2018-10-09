package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.CommitsMsgData;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CommitsMsgCommitList
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class CommitsMsgCommitList   {
  @JsonProperty("data")
  private CommitsMsgData data = null;

  @JsonProperty("isScanned")
  private Boolean isScanned = null;

  public CommitsMsgCommitList data(CommitsMsgData data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
  **/
  @ApiModelProperty(value = "")

  @Valid

  public CommitsMsgData getData() {
    return data;
  }

  public void setData(CommitsMsgData data) {
    this.data = data;
  }

  public CommitsMsgCommitList isScanned(Boolean isScanned) {
    this.isScanned = isScanned;
    return this;
  }

  /**
   * Get isScanned
   * @return isScanned
  **/
  @ApiModelProperty(value = "")


  public Boolean isIsScanned() {
    return isScanned;
  }

  public void setIsScanned(Boolean isScanned) {
    this.isScanned = isScanned;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommitsMsgCommitList commitsMsgCommitList = (CommitsMsgCommitList) o;
    return Objects.equals(this.data, commitsMsgCommitList.data) &&
        Objects.equals(this.isScanned, commitsMsgCommitList.isScanned);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, isScanned);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommitsMsgCommitList {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    isScanned: ").append(toIndentedString(isScanned)).append("\n");
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

