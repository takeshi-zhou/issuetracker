package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.domain.dto.RepoResourceDTO;
import cn.edu.fudan.measureservice.service.MeasureScanService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@MapperScan("cn.edu.fudan.measureservice.mapper")
public class MeasureServiceApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(MeasureServiceApplication.class, args);
    }

    @Autowired
    private MeasureScanService measureScanService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //test();
    }

    void test(){
        String repoId = "1336cb1c-5f6f-11ea-8d15-29e6fabaf15b";
        String branch = "master";
        String beginCommit = "23213e94f1d6b89a6a11d586501d961d22af936d";
        measureScanService.scan(RepoResourceDTO.builder().repoId(repoId).build(), branch, beginCommit, "javancss");
    }
}
