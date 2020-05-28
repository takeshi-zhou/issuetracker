package cn.edu.fudan.cloneservice.thread;

import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zyh
 * @date 2020/5/17
 */
public class MeasureTask implements Runnable {

    private String commitId;
    private String repoId;

    @Autowired
    private CloneMeasureService cloneMeasureService;

    public MeasureTask(String commitId, String repoId){

        this.repoId = repoId;
        this.commitId = commitId;
    }

    @Override
    public void run() {
        cloneMeasureService.insertCloneMeasure(repoId, commitId);
    }
}
