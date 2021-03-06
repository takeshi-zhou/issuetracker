package cn.edu.fudan.scanservice.component.strategy;

import org.springframework.beans.factory.annotation.Value;


public class ScanIntervalForStrategy {

    private static int interval;

    @Value("${coarse-grained.scan.timeBefore}")
    private int months;
    {
        if(months==0){
            interval=1;
        }else{
            interval=months;
        }
    }

    public static int getInterval() {
        return interval;
    }

    public static void setInterval(int interval) {
        ScanIntervalForStrategy.interval = interval;
    }
}
