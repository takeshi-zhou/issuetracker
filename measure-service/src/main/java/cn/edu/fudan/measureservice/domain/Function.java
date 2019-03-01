package cn.edu.fudan.measureservice.domain;

public class Function {
    private String name;
    private int ncss;
    private int ccn;
    private int javaDocs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNcss() {
        return ncss;
    }

    public void setNcss(int ncss) {
        this.ncss = ncss;
    }

    public int getCcn() {
        return ccn;
    }

    public void setCcn(int ccn) {
        this.ccn = ccn;
    }

    public int getJavaDocs() {
        return javaDocs;
    }

    public void setJavaDocs(int javaDocs) {
        this.javaDocs = javaDocs;
    }
}
