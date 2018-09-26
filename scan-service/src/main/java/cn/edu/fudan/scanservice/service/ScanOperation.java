package cn.edu.fudan.scanservice.service;

import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;

public interface ScanOperation {

    boolean isScanned(String commitId);

    boolean checkOut(String repoId, String commitId);

    ScanInitialInfo initialScan(String repoId, String commitId);

    ScanResult doScan(ScanInitialInfo scanInitialInfo);

    boolean mapping(String repoId, String commitId);

    boolean updateScan(ScanInitialInfo scanInitialInfo);

}
