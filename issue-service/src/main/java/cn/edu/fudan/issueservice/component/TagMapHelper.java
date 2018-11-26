package cn.edu.fudan.issueservice.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@Configuration
public class TagMapHelper {

    @Value("${immediate.tag_id}")
    private String immediate;
    @Value("${urgent.tag_id}")
    private String urgent;
    @Value("${high.tag_id}")
    private String high;
    @Value("${normal.tag_id}")
    private String normal;
    @Value("${low.tag_id}")
    private String low;
    @Value("${ignore.tag_id}")
    private String ignore;
    @Value("${solved.tag_id}")
    private String solved;
    @Value("${misinformation.tag_id}")
    private String misinformation;


    @Bean("tagMap")
    public Map<String,String> tagMapHelper(){
        Map<String,String> priorityToTagId=new HashMap<>();
        priorityToTagId.put("1",immediate);
        priorityToTagId.put("2",urgent);
        priorityToTagId.put("3",high);
        priorityToTagId.put("4",normal);
        priorityToTagId.put("5",low);
        priorityToTagId.put("6",ignore);
        priorityToTagId.put("7",solved);
        priorityToTagId.put("8",misinformation);
        return priorityToTagId;
    }
}
