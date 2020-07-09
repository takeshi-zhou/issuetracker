package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.domain.dto.RepoResourceDTO;
import cn.edu.fudan.measureservice.service.MeasureScanService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author fancying
 */
@SpringBootApplication
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
        //repoId = "1336cb1c-5f6f-11ea-8d15-29e6fabaf15b";
        String repoId = "test";
        String branch = "zhonghui20191012";
        String beginCommit = "6af96a970abc9f0d2e95730a133e208150035cc7";
        measureScanService.scan(RepoResourceDTO.builder().repoId(repoId).build(), branch, beginCommit, "javancss");
    }
}
