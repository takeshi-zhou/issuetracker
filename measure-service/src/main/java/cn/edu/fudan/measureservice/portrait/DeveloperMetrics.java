package cn.edu.fudan.measureservice.portrait;

/**
 * description: 开发者指标
 *
 * @author fancying
 * create: 2020-05-18 21:26
 **/
public class DeveloperMetrics extends BaseMetrics{

    private String developer;
    private Efficiency efficiency;
    private Quality quality;
    private Competence competence;

    public DeveloperMetrics(String developer, Efficiency efficiency, Quality quality, Competence competence) {
        this.developer = developer;
        this.efficiency = efficiency;
        this.quality = quality;
        this.competence = competence;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Efficiency getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Efficiency efficiency) {
        this.efficiency = efficiency;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }
}