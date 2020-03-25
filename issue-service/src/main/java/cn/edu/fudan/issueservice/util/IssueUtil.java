package cn.edu.fudan.issueservice.util;

import cn.edu.fudan.issueservice.domain.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IssueUtil {

    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${open.tag_id}")
    private String openTagId;
    @Value("${to_review.tag_id}")
    private String toReviewTagId;
    @Value("${ignore.tag_id}")
    private String ignoreTagId;
    @Value("${misinformation.tag_id}")
    private String misinformationTagId;

    public  String getTagIdByStatus(String status){
        String tagId = null;
        switch (StatusEnum.getStatusByName(status)){
            case IGNORE :
                tagId = ignoreTagId;
                break;
            case MISINFORMATION:
                tagId = misinformationTagId;
                break;
            case TO_REVIEW:
                tagId = toReviewTagId;
                break;
            case SOLVED:
                tagId = solvedTagId;
                break;
            case OPEN:
                tagId = openTagId;
                break;
            default:
                tagId = null;
        }
        return tagId;
    }
}
