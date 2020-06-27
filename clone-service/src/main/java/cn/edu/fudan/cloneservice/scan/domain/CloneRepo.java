package cn.edu.fudan.cloneservice.scan.domain;

import java.util.Date;

/**
 * @author zyh
 * @date 2020/6/22
 */
public class CloneRepo {

    private String uuid;
    private String repoId;
    private String startCommit;
    private String endCommit;
    private Integer totalCommitCount;
    private Integer scannedCommitCount;
    private Integer scanTime;
    private String status;
    private Date startScanTime;
    private Date endScanTime;
    /**
     * 记录扫描次数
     */
    private int scanCount;

    public CloneRepo(String uuid, String repoId, String startCommit, String endCommit, int totalCommitCount, int scannedCommitCount, int scanTime, String status, Date startScanTime, Date endScanTime, int scanCount) {
        this.uuid = uuid;
        this.repoId = repoId;
        this.startCommit = startCommit;
        this.endCommit = endCommit;
        this.totalCommitCount = totalCommitCount;
        this.scannedCommitCount = scannedCommitCount;
        this.scanTime = scanTime;
        this.status = status;
        this.startScanTime = startScanTime;
        this.endScanTime = endScanTime;
        this.scanCount = scanCount;
    }

    public CloneRepo(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getStartCommit() {
        return startCommit;
    }

    public void setStartCommit(String startCommit) {
        this.startCommit = startCommit;
    }

    public String getEndCommit() {
        return endCommit;
    }

    public void setEndCommit(String endCommit) {
        this.endCommit = endCommit;
    }

    public Integer getTotalCommitCount() {
        return totalCommitCount;
    }

    public void setTotalCommitCount(Integer totalCommitCount) {
        this.totalCommitCount = totalCommitCount;
    }

    public Integer getScannedCommitCount() {
        return scannedCommitCount;
    }

    public void setScannedCommitCount(Integer scannedCommitCount) {
        this.scannedCommitCount = scannedCommitCount;
    }

    public Integer getScanTime() {
        return scanTime;
    }

    public void setScanTime(Integer scanTime) {
        this.scanTime = scanTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartScanTime() {
        return startScanTime;
    }

    public void setStartScanTime(Date startScanTime) {
        this.startScanTime = startScanTime;
    }

    public Date getEndScanTime() {
        return endScanTime;
    }

    public void setEndScanTime(Date endScanTime) {
        this.endScanTime = endScanTime;
    }

    public Integer getScanCount() {
        return scanCount;
    }

    public void setScanCount(Integer scanCount) {
        this.scanCount = scanCount;
    }
}
