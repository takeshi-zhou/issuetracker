package cn.edu.fudan.measureservice.portrait;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.annotation.Retention;

/**
 * description: 开发人员效率
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
@Component
public class Efficiency {

    //提交频率评分
    private double commitFrequency;

    //代码量评分
    private double workLoad;

    //新增逻辑行评分
    private double newLogicLine;

    //删除逻辑行评分
    private double delLogicLine;

    //存活语句数评分
    private double validStatement;

    public Efficiency() {
    }

    public double getCommitFrequency() {
        return commitFrequency;
    }

    public void setCommitFrequency(double commitFrequency) {
        this.commitFrequency = commitFrequency;
    }

    public void setWorkLoad(double workLoad) {
        this.workLoad = workLoad;
    }

    public double getWorkLoad() {
        return workLoad;
    }

    public void setNewLogicLine(double newLogicLine) {
        this.newLogicLine = newLogicLine;
    }

    public double getNewLogicLine() {
        return newLogicLine;
    }

    public void setDelLogicLine(double delLogicLine) {
        this.delLogicLine = delLogicLine;
    }

    public double getDelLogicLine() {
        return delLogicLine;
    }

    public void setValidStatement(double validStatement) {
        this.validStatement = validStatement;
    }

    public double getValidStatement() {
        return validStatement;
    }
}