package cn.edu.fudan.measureservice.domain;

public class PackageAverage {

    private double classes;
    private double functions;
    private double ncss;
    private double javaDocs;

    public double getClasses() {
        return classes;
    }

    public void setClasses(double classes) {
        this.classes = classes;
    }

    public double getFunctions() {
        return functions;
    }

    public void setFunctions(double functions) {
        this.functions = functions;
    }

    public double getNcss() {
        return ncss;
    }

    public void setNcss(double ncss) {
        this.ncss = ncss;
    }

    public double getJavaDocs() {
        return javaDocs;
    }

    public void setJavaDocs(double javaDocs) {
        this.javaDocs = javaDocs;
    }
}
