package cn.edu.fudan.scanservice;

import cn.edu.fudan.scanservice.service.impl.ScanOperationAdapter;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ScanServiceApplicationTests {

    private final static Logger logger = LoggerFactory.getLogger(ScanOperationAdapter.class);

    @Autowired
    private HttpHeaders httpHeaders;

    @Value("${inner.service.path}")
    private String innerServicePath;

    protected RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Test
    public void contextLoads() {
        JSONObject requestParam = new JSONObject();
        requestParam.put("uuid", "9b60eadb-6738-4d73-acaa-114f4c848759");
        requestParam.put("scan_status", "Not Scanned");
        HttpEntity<Object> entity = new HttpEntity<>(requestParam, httpHeaders);
        restTemplate.exchange(innerServicePath + "/inner/project", HttpMethod.PUT, entity, JSONObject.class);
    }

    @Test
    public void test(){

    }

}
