package cn.edu.fudan.diffservice.tool;

import cn.edu.fudan.diffservice.entity.DiffParam;

public class TestDataMaker {
    public static DiffParam diffParamMaker(){
        DiffParam diffParam = new DiffParam();
        diffParam.setCommit1("dca3a362dc5e8dfd96ac9fef4dab86685d3fd018");
        diffParam.setCommit2("6aa83fc7d6e47d3c3a2c56c800f0a729a57563c9");
        diffParam.setFilePath1("FudanSELab/IssueTracker-Master/clone-service/src/main/java/cn/edu/fudan/cloneservice/domain/Scan.java");
        diffParam.setFilePath2("FudanSELab/IssueTracker-Master/clone-service/src/main/java/cn/edu/fudan/cloneservice/util/DateTimeUtil.java");
        diffParam.setRepoid("d793c176-d2d1-11e8-bcd5-d067e5ea858d");
        return diffParam;
    }
}
