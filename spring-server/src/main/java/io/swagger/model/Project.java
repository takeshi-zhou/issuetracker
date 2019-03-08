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
 * Project
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-04T08:22:59.607Z")

public class Project   {
  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("language")
  private String language = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("vcs_type")
  private String vcsType = null;

  @JsonProperty("account_id")
  private String accountId = null;

  @JsonProperty("prev_scan_commit")
  private String prevScanCommit = null;

  @JsonProperty("download_status")
  private String downloadStatus = null;

  @JsonProperty("scan_status")
  private String scanStatus = null;

  @JsonProperty("till_commit_time")
  private String tillCommitTime = null;

  @JsonProperty("last_scan_time")
  private String lastScanTime = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("repo_id")
  private String repoId = null;

  public Project uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * Get uuid
   * @return uuid
  **/
  @ApiModelProperty(value = "")


  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Project name(String name) {
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

  public Project language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
  **/
  @ApiModelProperty(value = "")


  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Project url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
  **/
  @ApiModelProperty(value = "")


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Project vcsType(String vcsType) {
    this.vcsType = vcsType;
    return this;
  }

  /**
   * Get vcsType
   * @return vcsType
  **/
  @ApiModelProperty(value = "")


  public String getVcsType() {
    return vcsType;
  }

  public void setVcsType(String vcsType) {
    this.vcsType = vcsType;
  }

  public Project accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
  **/
  @ApiModelProperty(value = "")


  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public Project prevScanCommit(String prevScanCommit) {
    this.prevScanCommit = prevScanCommit;
    return this;
  }

  /**
   * Get prevScanCommit
   * @return prevScanCommit
  **/
  @ApiModelProperty(value = "")


  public String getPrevScanCommit() {
    return prevScanCommit;
  }

  public void setPrevScanCommit(String prevScanCommit) {
    this.prevScanCommit = prevScanCommit;
  }

  public Project downloadStatus(String downloadStatus) {
    this.downloadStatus = downloadStatus;
    return this;
  }

  /**
   * Get downloadStatus
   * @return downloadStatus
  **/
  @ApiModelProperty(value = "")


  public String getDownloadStatus() {
    return downloadStatus;
  }

  public void setDownloadStatus(String downloadStatus) {
    this.downloadStatus = downloadStatus;
  }

  public Project scanStatus(String scanStatus) {
    this.scanStatus = scanStatus;
    return this;
  }

  /**
   * Get scanStatus
   * @return scanStatus
  **/
  @ApiModelProperty(value = "")


  public String getScanStatus() {
    return scanStatus;
  }

  public void setScanStatus(String scanStatus) {
    this.scanStatus = scanStatus;
  }

  public Project tillCommitTime(String tillCommitTime) {
    this.tillCommitTime = tillCommitTime;
    return this;
  }

  /**
   * Get tillCommitTime
   * @return tillCommitTime
  **/
  @ApiModelProperty(value = "")


  public String getTillCommitTime() {
    return tillCommitTime;
  }

  public void setTillCommitTime(String tillCommitTime) {
    this.tillCommitTime = tillCommitTime;
  }

  public Project lastScanTime(String lastScanTime) {
    this.lastScanTime = lastScanTime;
    return this;
  }

  /**
   * Get lastScanTime
   * @return lastScanTime
  **/
  @ApiModelProperty(value = "")


  public String getLastScanTime() {
    return lastScanTime;
  }

  public void setLastScanTime(String lastScanTime) {
    this.lastScanTime = lastScanTime;
  }

  public Project description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(value = "")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project repoId(String repoId) {
    this.repoId = repoId;
    return this;
  }

  /**
   * Get repoId
   * @return repoId
  **/
  @ApiModelProperty(value = "")


  public String getRepoId() {
    return repoId;
  }

  public void setRepoId(String repoId) {
    this.repoId = repoId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Project project = (Project) o;
    return Objects.equals(this.uuid, project.uuid) &&
        Objects.equals(this.name, project.name) &&
        Objects.equals(this.language, project.language) &&
        Objects.equals(this.url, project.url) &&
        Objects.equals(this.vcsType, project.vcsType) &&
        Objects.equals(this.accountId, project.accountId) &&
        Objects.equals(this.prevScanCommit, project.prevScanCommit) &&
        Objects.equals(this.downloadStatus, project.downloadStatus) &&
        Objects.equals(this.scanStatus, project.scanStatus) &&
        Objects.equals(this.tillCommitTime, project.tillCommitTime) &&
        Objects.equals(this.lastScanTime, project.lastScanTime) &&
        Objects.equals(this.description, project.description) &&
        Objects.equals(this.repoId, project.repoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, language, url, vcsType, accountId, prevScanCommit, downloadStatus, scanStatus, tillCommitTime, lastScanTime, description, repoId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Project {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    vcsType: ").append(toIndentedString(vcsType)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    prevScanCommit: ").append(toIndentedString(prevScanCommit)).append("\n");
    sb.append("    downloadStatus: ").append(toIndentedString(downloadStatus)).append("\n");
    sb.append("    scanStatus: ").append(toIndentedString(scanStatus)).append("\n");
    sb.append("    tillCommitTime: ").append(toIndentedString(tillCommitTime)).append("\n");
    sb.append("    lastScanTime: ").append(toIndentedString(lastScanTime)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    repoId: ").append(toIndentedString(repoId)).append("\n");
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

