package cn.edu.fudan.issueservice.domain.statistics;

import java.util.ArrayList;
import java.util.List;

public class CodeQualityResponse {
    private int totalCountQualities;
    private int page;
    private int ps;

    private Quality totalQuality;

    List<TimeQuality> qualities = new ArrayList<>();

    List<DeveloperQuality> developers = new ArrayList<>();


    public int getTotalCountQualities() {
        return totalCountQualities;
    }

    public void setTotalCountQualities(int totalCountQualities) {
        this.totalCountQualities = totalCountQualities;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public List<TimeQuality> getQualities() {
        return qualities;
    }

    public void setQualities(List<TimeQuality> qualities) {
        this.qualities = qualities;
    }

    public List<DeveloperQuality> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<DeveloperQuality> developers) {
        this.developers = developers;
    }

    public void addDeveloperQuality(DeveloperQuality developerQuality) {
        this.developers.add(developerQuality);
    }


    public Quality getTotalQuality() {
        return totalQuality;
    }

    public void setTotalQuality(Quality totalQuality) {
        this.totalQuality = totalQuality;
    }
}
