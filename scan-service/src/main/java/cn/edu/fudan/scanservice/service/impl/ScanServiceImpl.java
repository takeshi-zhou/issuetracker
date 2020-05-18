package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.ScanService;
import cn.edu.fudan.scanservice.util.JGitHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ScanServiceImpl implements ScanService {

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

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
    public void deleteScanByRepoIdAndCategory(String repoId,String category) {
        scanDao.deleteScanByRepoIdAndCategory(repoId,category);
    }

    @Override
    public void updateOneScan(Scan scan) {
        scanDao.updateOneScan(scan);
    }

    @Override
    public Object getCommits(String project_id, Integer page, Integer size, Boolean is_whole,String category) {
        String repo_id = restInterfaceManager.getRepoIdOfProject(project_id);
        JSONObject commitResponse = restInterfaceManager.getCommitsOfRepo(repo_id,page,size);
        if (commitResponse != null) {
            List<String> scannedCommitId = scanDao.getScannedCommits(repo_id,category);
            JSONArray commitArray = commitResponse.getJSONArray("data");
            int index = 0;
            if (scannedCommitId.isEmpty()) {
                //全都没扫过
                for (int i = 0; i < commitArray.size(); i++) {
                    JSONObject commit = commitArray.getJSONObject(i);
                    commit.put("isScanned", false);

                }
                is_whole = true;
            } else {
                String lastScannedCommitId = scannedCommitId.get(scannedCommitId.size() - 1);
                //add for disable commits befor scanning commits
                boolean disabled = false;
                for (int i = 0; i < commitArray.size(); i++) {
                    JSONObject commit = commitArray.getJSONObject(i);
                    String commit_id = commit.getString("commit_id");
                    if (scannedCommitId.contains(commit_id) || disabled) {
                        commit.put("isScanned", true);
                        if (commit_id.equals(lastScannedCommitId)) {
                            index = i;
                        }
                        disabled = true;
                    } else {
                        commit.put("isScanned", false);
                    }
                }
            }
            Map<String, Object> result = new HashMap<>();
            if (is_whole) {
                int totalCount = commitArray.size();
                result.put("totalCount", totalCount);
                if (totalCount > size) {
                    result.put("commitList", commitArray.subList((page - 1) * size, page * size > totalCount ? totalCount : page * size));
                } else {
                    result.put("commitList", commitArray);
                }
            } else {
                List<Object> notScannedCommits = commitArray.subList(0, index);
                int totalCount = notScannedCommits.size();
                result.put("totalCount", totalCount);
                if (totalCount > size) {
                    result.put("commitList", notScannedCommits.subList((page - 1) * size, page * size > totalCount ? totalCount : page * size));
                } else {
                    result.put("commitList", notScannedCommits);
                }
            }
            return result;
        } else {
            throw new RuntimeException("commit query failed!");
        }
    }

    @Override
    public Object getScannedCommits(String repoId,String category) {
        return scanDao.getScans(repoId,category);
    }

    @Override
    public String getLatestScannedCommitId(String repo_id, String category) {

        return scanDao.getLatestScannedCommitId(repo_id, category);
    }

    @Override
    public String getNextScannedCommitID(String repo_id, String category, String commitId) {
        String nextScannedCommitId="";
        List<String> scannedCommitId = scanDao.getScannedCommits(repo_id,category);
        if(scannedCommitId==null||scannedCommitId.isEmpty()) {
            return nextScannedCommitId;
        }
        for(int i=0;i<scannedCommitId.size();i++){
            if(scannedCommitId.get(i).equals(commitId)){
                if(i+1<scannedCommitId.size()){
                    nextScannedCommitId=scannedCommitId.get(i+1);
                    break;
                }
            }
        }
        return nextScannedCommitId;
    }

    @Override
    public String getPreviousScannedCommitID(String repo_id, String category, String commitId) {
        String previousScannedCommitId="";
        List<String> scannedCommitId = scanDao.getScannedCommits(repo_id,category);
        if(scannedCommitId==null||scannedCommitId.isEmpty()) {
            return previousScannedCommitId;
        }
        for(int i=0;i<scannedCommitId.size();i++){
            if(scannedCommitId.get(i).equals(commitId)){
                if(i>0){
                    previousScannedCommitId=scannedCommitId.get(i-1);
                    break;
                }
            }
        }
        return previousScannedCommitId;
    }

    @Override
    public Scan getScanByCategoryAndRepoIdAndCommitId(String repo_id, String category, String commit_id) {
        return  scanDao.getScanByCategoryAndRepoIdAndCommitId(repo_id, category,commit_id);
    }

    @Override
    public List<String> getPreScannedCommitByCurrentCommit(String repoId, String category, String commitId) throws RuntimeException{
        String repoPath = null;
        List<String> result = null;
        JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId, 1, 1, null);
         if(jsonObject == null){
            throw new RuntimeException("request base server failed");
        }
        JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
        JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
        String checkCommitId = latestScanMessageWithTime.getString("commit_id");
        try{
            repoPath = restInterfaceManager.getRepoPath(repoId,checkCommitId);
            if(repoPath == null){
                throw new RuntimeException("request base server failed");
            }
            JGitHelper jGitHelper = new JGitHelper(repoPath);
            result = getPreScannedCommitByJGit(jGitHelper,null,repoId,category,commitId);
        }finally{
            restInterfaceManager.freeRepoPath(repoId,repoPath);
        }
        return result;
    }




    private List<String> getPreScannedCommitByJGit(JGitHelper jGitHelper, List<String> scannedParents, String repoId, String category, String commitId) {
        if(scannedParents == null){
            scannedParents = new ArrayList<>();
        }

        List<Scan>  scanList = scanDao.getScanByRepoIdAndStatusAndCategory(repoId,null,category);

        List<String> scanIds = new LinkedList<>();
        for(Scan scan : scanList){
            scanIds.add(scan.getUuid());
        }

        LinkedList<String> parentCommits = new LinkedList<>();
        parentCommits.addFirst(commitId);
        while (!parentCommits.isEmpty()){
            String commit = parentCommits.removeFirst();
            String[] parents = jGitHelper.getCommitParents(commit);
            for(String parent : parents){

                if(!scanIds.contains(parent)){
                    continue;
                }

                for(Scan scan : scanList){
                    if(scan.getCommit_id().equals(parent)){
                        if("done".equals(scan.getStatus())){
                            if(!scannedParents.contains(parent)){
                                scannedParents.add(parent);
                            }
                        }else{
                            parentCommits.addFirst(parent);
                        }
                        break;
                    }
                }


            }
        }
        return scannedParents;
    }


    @Override
    public String getLatestScanFailedCommitIdAndDeveloper(String repoId, String category, String commitId) throws RuntimeException {
        String repoPath = null;
        String result = null;
        try{
            repoPath = restInterfaceManager.getRepoPath(repoId,commitId);
            if(repoPath == null){
                throw new RuntimeException("request base server failed");
            }
            JGitHelper jGitHelper = new JGitHelper(repoPath);
            result = getPreScannedFailedCommitByJGit(jGitHelper,repoId,category,commitId);
        }finally{
            restInterfaceManager.freeRepoPath(repoId,repoPath);
        }
        return result;
    }

    @Override
    public Object getScanByRepoIdAndStatusAndCategory(String repoId, String status,String category) {
        return scanDao.getScanByRepoIdAndStatusAndCategory(repoId,status,category);
    }

    /**
     * 目前只是选取第一个未扫描或者编译未成功的commit作为返回值。 后面仍需改进。
     * @param jGitHelper
     * @param repoId
     * @param category
     * @param commitId
     * @return
     */
    private String getPreScannedFailedCommitByJGit(JGitHelper jGitHelper, String repoId, String category, String commitId) {

        String[] parents = jGitHelper.getCommitParents(commitId);
        for(String parent : parents){
            Scan  scan = scanDao.getScanByCategoryAndRepoIdAndCommitId(repoId,category,parent);
            if(scan == null ){
                return parent;
            }
            if("done".equals(scan.getStatus())){
                continue;
            }else{
                return parent;
            }
        }
        return null;
    }
}
