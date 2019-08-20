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
    public void testCloneInfo(){
        String repoId = "4f696ccc-65ef-11e9-9ddc-f93dfaa9da61";
        String commit_id = "03f353e39d23c71825405acd22db94428323997b";
        List<CloneInfo> lci = cloneInfoDao.getCloneInfoByRepoIdAndCommitId(repoId, commit_id);
        System.out.println(lci.size());


    }
}
