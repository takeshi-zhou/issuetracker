package cn.edu.fudan.measureservice.portrait;

import com.sun.xml.internal.bind.api.impl.NameConverter;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
public class Quality {
    private double standard;
    private double security;
    private double issueRate;
    private double issueDensity;

    public Quality() {
    }

    public double getStandard() {
        return standard;
    }

    public void setStandard(double standard) {
        this.standard = standard;
    }

    public double getSecurity() {
        return security;
    }

    public void setSecurity(double security) {
        this.security = security;
    }

    public double getIssueRate() {
        return issueRate;
    }

    public void setIssueRate(double issueRate) {
        this.issueRate = issueRate;
    }

    public double getIssueDensity() {
        return issueDensity;
    }

    public void setIssueDensity(double issueDensity) {
        this.issueDensity = issueDensity;
    }
}