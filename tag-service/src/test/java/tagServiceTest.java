import cn.edu.fudan.tagservice.domain.Priority;
import org.junit.Test;

public class tagServiceTest {

    @Test
    public void test(){
        Priority priority =Priority.getByValue("High");

        System.out.println(priority.toString());
        System.out.println(priority.getColor());
    }
}
