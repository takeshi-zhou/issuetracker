package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ScanServiceImpl implements ScanService {


    private ScanDao scanDao;

    @Autowired
    public void setScanDao(ScanDao scanDao) {
        this.scanDao = scanDao;
    }

    @Override
    public void insertOneScan(Scan scan) {
        scanDao.insertOneScan(scan);
    }

    @Override
    public void deleteScanByProjectId(String projectId) {
        scanDao.deleteScanByProjectId(projectId);
    }

    @Override
    public void updateOneScan(Scan scan) {
        scanDao.updateOneScan(scan);
    }

    @Override
    public String getLatestScannedCommitId(String project_id) {
        return scanDao.getLatestScannedCommitId(project_id);
    }

    @Override
    public Object getTillCommitDateByProjectId(String projectId) {
        return scanDao.getTillCommitDateByProjectId(projectId);
    }
}
