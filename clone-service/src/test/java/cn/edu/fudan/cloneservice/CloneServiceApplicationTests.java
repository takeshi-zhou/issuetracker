package cn.edu.fudan.cloneservice;



import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.CloneInfoDao;
import cn.edu.fudan.cloneservice.dao.CommitDao;
import cn.edu.fudan.cloneservice.dao.PackageNameDao;
import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.domain.Commit;
import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.mapper.PackageNameMapper;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private PackageNameMapper packageNameMapper;
    @Autowired
    private RestInterfaceManager restInterfaceManager;
    @Autowired
    private CloneInfoDao cloneInfoDao;
    @Autowired
    private CommitDao commitDao;
    @Test
    @SuppressWarnings("unchecked")
    public void testDao(){
        String test = "test";
        String repo_id =  "4f696ccc-65ef-11e9-9ddc-f93dfaa9da61";
        List<String> lc = new ArrayList<>();
        lc=        commitDao.getCommitList(repo_id);

    }
    @Test
    public void testRestful() {
        String repo_id = "700f4bf2-5f25-11e9-bd27-77953a60dffa";
        String commit_id = "922dc95453760d98b0ccb00a08239f0a5b695bf0";

        JSONObject jsonObject=  restInterfaceManager.getURL(repo_id, commit_id);
        JSONObject data= jsonObject.getJSONObject("data");
        String status = data.get("status").toString();
        String content = data.get("content").toString();
        if(status.equals("Successful")){
            System.out.println(content);

            JSONObject nj =restInterfaceManager.freeRepoPath(repo_id,content);
            JSONObject njdata = nj.getJSONObject("data");
            String s = njdata.get("status").toString();
            if(s.equals("Successful")){

            }

        }else {

            System.out.println("Error");
        }

    }

    @Test
    public void testCloneInfo(){
        String repoId = "4f696ccc-65ef-11e9-9ddc-f93dfaa9da61";
        String commit_id = "03f353e39d23c71825405acd22db94428323997b";
        List<CloneInfo> lci = cloneInfoDao.getCloneInfoByRepoIdAndCommitId(repoId, commit_id);
        System.out.println(lci.size());


    }
}
