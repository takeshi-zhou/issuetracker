import cn.edu.fudan.tagservice.TagServiceApplication;
import cn.edu.fudan.tagservice.domain.ModifyTaggedItem;
import cn.edu.fudan.tagservice.service.TagService;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TagServiceApplication.class)
public class TagServiceApplicationTests {

    private TagService tagService;

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Test
    public void modifyTags(){
        List<ModifyTaggedItem> list = new ArrayList<>();
        ModifyTaggedItem m1 = new ModifyTaggedItem("2d5cefde-40df-428a-b4ad-8ed54bf42467","f123a572-253a-41aa-bf63-732aa3c4e230","a890f64d-c485-4259-b9a3-8cb702843145");
        list.add(m1);
        tagService.modifyMultiTaggedItem(list);
        System.out.println(1);
    }
}
