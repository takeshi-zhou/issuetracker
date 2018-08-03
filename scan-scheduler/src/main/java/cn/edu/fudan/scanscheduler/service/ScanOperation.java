package cn.edu.fudan.scanscheduler.service;

import cn.edu.fudan.scanscheduler.domain.ScanInitialInfo;
import cn.edu.fudan.scanscheduler.domain.ScanResult;

public interface ScanOperation {

    boolean isScanned(String commitId);

    boolean checkOut(String projectId, String commitId);

    ScanInitialInfo initialScan(String projectId, String commitId);

    ScanResult doScan(ScanInitialInfo scanInitialInfo);

    boolean mapping(String projectId, String commitId);

    boolean updateScan(ScanInitialInfo scanInitialInfo);

}
