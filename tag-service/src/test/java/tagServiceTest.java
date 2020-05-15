import cn.edu.fudan.tagservice.domain.IgnoreLevelEnum;
import cn.edu.fudan.tagservice.domain.PriorityEnum;
import org.junit.Test;

import java.util.UUID;

public class tagServiceTest {

    @Test
    public void test() {

        PriorityEnum priority = PriorityEnum.getByValue("HIGH");

        System.out.println(priority.toString());
        System.out.println(priority.getColor());

        System.out.println(UUID.randomUUID().toString());

        int i = IgnoreLevelEnum.valueOf("USER").value();
        System.out.println(i);
    }
}
