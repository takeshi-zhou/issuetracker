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
 * RequestBody
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-09T08:48:10.049Z")

public class RequestBody   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("scope")
  private String scope = null;

  @JsonProperty("itemId")
  private String itemId = null;

  @JsonProperty("oldName")
  private String oldName = null;

  @JsonProperty("isDefault")
  private Boolean isDefault = null;

  public RequestBody name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RequestBody scope(String scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Get scope
   * @return scope
  **/
  @ApiModelProperty(value = "")


  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public RequestBody itemId(String itemId) {
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

  public RequestBody oldName(String oldName) {
    this.oldName = oldName;
    return this;
  }

  /**
   * Get oldName
   * @return oldName
  **/
  @ApiModelProperty(value = "")


  public String getOldName() {
    return oldName;
  }

  public void setOldName(String oldName) {
    this.oldName = oldName;
  }

  public RequestBody isDefault(Boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  /**
   * Get isDefault
   * @return isDefault
  **/
  @ApiModelProperty(value = "")


  public Boolean isIsDefault() {
    return isDefault;
  }

  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestBody requestBody = (RequestBody) o;
    return Objects.equals(this.name, requestBody.name) &&
        Objects.equals(this.scope, requestBody.scope) &&
        Objects.equals(this.itemId, requestBody.itemId) &&
        Objects.equals(this.oldName, requestBody.oldName) &&
        Objects.equals(this.isDefault, requestBody.isDefault);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, scope, itemId, oldName, isDefault);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequestBody {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    oldName: ").append(toIndentedString(oldName)).append("\n");
    sb.append("    isDefault: ").append(toIndentedString(isDefault)).append("\n");
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

