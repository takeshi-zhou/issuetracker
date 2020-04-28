package cn.edu.fudan.cloneservice.domain;

import java.util.Map;

/**
 * @author zyh
 * @date 2020/4/28
 */
public class CommitChange {

    private int addLines;
    private Map<String, String> addMap;

    public int getAddLines() {
        return addLines;
    }

    public void setAddLines(int addLines) {
        this.addLines = addLines;
    }

    public Map<String, String> getAddMap() {
        return addMap;
    }

    public void setAddMap(Map<String, String> addMap) {
        this.addMap = addMap;
    }
}
