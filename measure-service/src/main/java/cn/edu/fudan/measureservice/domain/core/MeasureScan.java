package cn.edu.fudan.measureservice.domain.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasureScan {
    String uuid;
    String repoId;
    String tool;
    String start_commit;
    String end_commit;
    int total_commit_count;
    int scanned_commit_count;
    int scan_time;
    String status;
    Date start_scan_time;
    Date end_scan_time;
}
