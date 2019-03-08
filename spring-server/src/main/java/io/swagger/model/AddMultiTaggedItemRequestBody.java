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
 * AddMultiTaggedItemRequestBody
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class AddMultiTaggedItemRequestBody   {
  @JsonProperty("item_id")
  private String itemId = null;

  @JsonProperty("tag_id")
  private String tagId = null;

  public AddMultiTaggedItemRequestBody itemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  /**
   * Get itemId
   * @return itemId
  **/
  @ApiModelProperty(value = "")


  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public AddMultiTaggedItemRequestBody tagId(String tagId) {
    this.tagId = tagId;
    return this;
  }

  /**
   * Get tagId
   * @return tagId
  **/
  @ApiModelProperty(value = "")


  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddMultiTaggedItemRequestBody addMultiTaggedItemRequestBody = (AddMultiTaggedItemRequestBody) o;
    return Objects.equals(this.itemId, addMultiTaggedItemRequestBody.itemId) &&
        Objects.equals(this.tagId, addMultiTaggedItemRequestBody.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, tagId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddMultiTaggedItemRequestBody {\n");
    
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    tagId: ").append(toIndentedString(tagId)).append("\n");
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

