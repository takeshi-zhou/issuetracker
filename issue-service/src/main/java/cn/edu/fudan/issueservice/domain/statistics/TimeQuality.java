package cn.edu.fudan.issueservice.domain.statistics;

import java.time.LocalDate;

public class TimeQuality extends Quality {

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
