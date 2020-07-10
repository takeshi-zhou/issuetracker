package cn.edu.fudan.scanservice.tools;

import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.util.ExecuteShellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("sonar")
public class SonarQubeScanOperation extends ScanOperationAdapter  {

    private final static Logger logger = LoggerFactory.getLogger(SonarQubeScanOperation.class);

    private ExecuteShellUtil executeShellUtil;

    @Autowired
    public void setExecuteShellUtil(ExecuteShellUtil executeShellUtil) {
        this.executeShellUtil = executeShellUtil;
    }

    private boolean compile(String repoPath) {
        return executeShellUtil.executeMvn(repoPath);
    }

    private boolean executeSonar(String repoPath, String projectName,String version) {
        return executeShellUtil.executeSonar(repoPath, projectName,version);
    }

    @Override
    public ScanResult doScan(ScanInitialInfo scanInitialInfo) {
        Scan scan = scanInitialInfo.getScan();
        String repoPath = scanInitialInfo.getRepoPath();
        String repoName = scanInitialInfo.getRepoName();
        String version = scanInitialInfo.getScan().getCommit_time().toString();

        logger.info("start to compile the repository ->" + repoPath);
        if (!compile(repoPath)) {
            logger.error("Project Compile Failed!");
            return new ScanResult("Sonar","failed", "compile failed");
        }
        logger.info("compile complete");
        logger.info("start to invoke Sonar to scan......");
        //如果编译成功,调用sonar-scanner对classes文件进行扫描
        if (!executeSonar(repoPath, repoName,version)) {
            logger.error("Invoke Sonar  Failed!");
            return new ScanResult("Sonar","failed", "Sonar invoke failed");
        }
        logger.info("scan complete");
        logger.info("start to analyze result......");
        //如果sonar-scanner调用成功，开始通过api访问的方式获取缺陷数据
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!analyzeSonarResult(repoName)) {
            logger.error("analyze sonar result failed");
            return new ScanResult("Sonar","failed", "analyze failed");
        }
        logger.info("sonar analyze complete");
        logger.info("need to wait for the update of data...");
        try {
            TimeUnit.SECONDS.sleep( 30 );
        } catch (InterruptedException e) {
            logger.error("Thread error when execute  sonar scan ");

        }
        return new ScanResult("Sonar","success", "Scan Success!");
    }

    private boolean analyzeSonarResult(String repoName){
        int pageSize=100;
        JSONObject sonarIssues = restInterfaceManager.getSonarIssueResults(repoName,null,pageSize,false);
        if(sonarIssues == null){
            return false;
        }
        //如果components的size为0代表scanner 扫描项目结果失败了
        JSONArray components = sonarIssues.getJSONArray("components");
        if(components.size() == 0){
            logger.error("use sonar scanner to analyze ---> {}  failed",repoName);
            return false;
        }

        return true;
    }


}
