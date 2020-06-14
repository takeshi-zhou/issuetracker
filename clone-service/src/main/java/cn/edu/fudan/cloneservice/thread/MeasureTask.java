package cn.edu.fudan.cloneservice.thread;

import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.service.impl.CloneMeasureServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zyh
 * @date 2020/5/17
 */
public class MeasureTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MeasureTask.class);

    private String commitId;
    private String repoId;

    private CloneMeasureService cloneMeasureService;

    public MeasureTask(String commitId, String repoId){

        this.repoId = repoId;
        this.commitId = commitId;

        cloneMeasureService = new CloneMeasureServiceImpl();
    }

    @Override
    public void run() {
        logger.info("{}-> start measure", Thread.currentThread().getName());
        cloneMeasureService.insertCloneMeasure(repoId, commitId);
        logger.info("{}-> measure complete", Thread.currentThread().getName());
    }
}
