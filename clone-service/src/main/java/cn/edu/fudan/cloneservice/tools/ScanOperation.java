package cn.edu.fudan.cloneservice.tools;

import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.domain.ScanResult;

public interface ScanOperation {

    boolean isScanned(String repoId,String commitId,String category);

    ScanInitialInfo initialScan(String repoId, String commitId, String category);

    ScanResult doScan(ScanInitialInfo scanInitialInfo);

    boolean updateScan(ScanInitialInfo scanInitialInfo);
}
