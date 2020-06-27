package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Packages {
    private List<Package> packages;
    private PackageAverage packageAverage;
}
