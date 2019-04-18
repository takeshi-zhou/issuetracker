package cn.edu.fudan.cloneservice;



import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.PackageNameDao;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private PackageNameMapper packageNameMapper;
    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Test
    @SuppressWarnings("unchecked")
    public void testDao(){
        String test = "test";
        PackageInfo packageInfo = new PackageInfo(test, test,test,test,1,1,1);

//        packageNameMapper.insertPackageNameSetByRepoIdAndCommitId(packageInfo);


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
}
