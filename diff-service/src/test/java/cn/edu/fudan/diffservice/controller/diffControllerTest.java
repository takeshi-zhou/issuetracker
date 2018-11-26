package cn.edu.fudan.diffservice.controller;

import cn.edu.fudan.diffservice.DiffserviceApplicationTests;
import cn.edu.fudan.diffservice.entity.DiffParam;
import cn.edu.fudan.diffservice.entity.ResponseBean;
import cn.edu.fudan.diffservice.service.DiffService;
import cn.edu.fudan.diffservice.tool.TestDataMaker;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@PrepareForTest({diffController.class, DiffService.class})
@WebAppConfiguration
public class diffControllerTest extends DiffserviceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    DiffService diffService;

    @Autowired
    @InjectMocks
    diffController controller;

    private MockMvc mockMvc;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    private ResponseBean responseBean ;
    private DiffParam diffParam;
    ObjectMapper mapper=new ObjectMapper();

    @Before
    public void setupMockMvcAndData() throws Exception {
        diffService = Mockito.mock(DiffService.class);
    //    mapper = new ObjectMapper();
        MemberModifier.field(diffController.class, "diffService").set(controller, diffService);
        //setupMockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

        responseBean = new ResponseBean();
        diffParam = TestDataMaker.diffParamMaker();
    }


    @Test
    public void testStartDiffSuccess() throws Exception {
    //TODO: Test goes here...
        String dirPath = "/home/fdse/user/issueTracker/diffpath/";

        responseBean.setCode(200);
        responseBean.setData(null);
        responseBean.setMsg("diffservice success!");

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(diffParam);
        PowerMockito.when(diffService.diffTwoFile(dirPath,diffParam.getRepoid(),diffParam.getCommit1(),diffParam.getFilePath1())).thenReturn(null);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/diffservice/twofilediff")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();

        ResponseBean responseBeanResult = JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
        Assert.assertEquals(200, responseBeanResult.getCode());
}

    @Test
    public void testStartDiffFail() throws Exception {
        String dirPath = "/home/fdse/user/issueTracker/diffpath/";

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(diffParam);

        PowerMockito.when(diffService, "diffTwoFile", dirPath,diffParam.getRepoid(),diffParam.getCommit1(),diffParam.getFilePath1()).thenThrow(new RuntimeException());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(
                "/diffservice/twofilediff")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();
        ResponseBean responseBeanResult = JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
        Assert.assertEquals(401, responseBeanResult.getCode());
 }

} 
