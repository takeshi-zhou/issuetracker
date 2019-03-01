package cn.edu.fudan.measureservice.domain;

import java.util.List;

public class Packages {
    private List<Package> packages;
    private PackageAverage packageAverage;

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public PackageAverage getPackageAverage() {
        return packageAverage;
    }

    public void setPackageAverage(PackageAverage packageAverage) {
        this.packageAverage = packageAverage;
    }
}
