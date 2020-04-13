package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.domain.IssueType;
import cn.edu.fudan.issueservice.service.IssueTypeService;
import cn.edu.fudan.issueservice.service.impl.IssueTypeServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-develop.properties")
@SpringBootTest
public class IssueTrackerApplicationTests {

    @Autowired
    RestInterfaceManager restInterfaceManager;

    @Autowired
    private IssueTypeService issueTypeService = new IssueTypeServiceImpl();


    @Test
    public void addTags(){
        List<String> tags = new ArrayList<>();
        tags.add("a890f64d-c485-4259-b9a3-8cb702843145");

        restInterfaceManager.getSolvedIssueIds(tags);
        System.out.println("result");
    }


    @Test
    public  void addIssueTypeFromSonar(){
        List<IssueType> issueTypes = new ArrayList<>();
        JSONObject sonarResult = restInterfaceManager.getSonarIssueType("squid","READY",1,300);
        JSONArray sonarRuleJson = sonarResult.getJSONArray("rules");
        for(int i = 0; i<sonarRuleJson.size(); i++){
            JSONObject sonarRule = sonarRuleJson.getJSONObject(i);
            IssueType issueType = new IssueType();
            issueType.setUuid(UUID.randomUUID().toString());
            issueType.setType(sonarRule.getString("name"));
            issueType.setTool("SonarQube");

            String type = sonarRule.getString("type");
            issueType.setCategory(type);
            String mdDesc = sonarRule.getString("mdDesc");
            issueType.setDescription(mdDesc);
            issueType.setLanguage("java");
            issueTypes.add(issueType);
        }

        issueTypeService.insertIssueList(issueTypes);
    }


    @Test
    public  void getTagByCondition(){
        List<IssueType> issueTypes = new ArrayList<>();
        JSONArray result = restInterfaceManager.getTagByCondition(null,"Security hotspot",null);

        System.out.println(1);
    }

}
