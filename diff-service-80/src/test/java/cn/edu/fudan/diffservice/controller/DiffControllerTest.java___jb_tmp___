package cn.edu.fudan.diffservice.controller;

import cn.edu.fudan.diffservice.DiffServiceApplicationTests;
import cn.edu.fudan.diffservice.service.ClearCacheHandler;
import cn.edu.fudan.diffservice.service.FetchFileContentHandler;
import cn.edu.fudan.diffservice.service.FetchMetaCacheHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

@PrepareForTest({DiffController.class, FetchMetaCacheHandler.class, FetchFileContentHandler.class,
        ClearCacheHandler.class})
@WebAppConfiguration
public class DiffControllerTest extends DiffServiceApplicationTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    FetchMetaCacheHandler fetchMetaCacheHandler;
    @Autowired
    FetchFileContentHandler fetchFileContentHandler;
    @Autowired
    ClearCacheHandler clearCacheHandler;

    @Autowired
    @InjectMocks
    DiffController controller;

    private MockMvc mockMvc;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    private String metaParam;
    ObjectMapper mapper=new ObjectMapper();

    @Before
    public void setupMockMvcAndData() throws Exception{
        fetchMetaCacheHandler = Mockito.mock(FetchMetaCacheHandler.class);
        MemberModifier.field(DiffController.class, "fetchMetaCacheHandler").set(controller, fetchMetaCacheHandler);

        //setupMockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();
    }

    @Test
    public void fetchMetaCacheHandlerSuccess() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(metaParam);
        PowerMockito.when(fetchMetaCacheHandler.handle(metaParam)).thenReturn(null);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/Diffservice/fetchMeta")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();
        System.out.println("Success: "+result);
        String meteisnull = "";
        if (result.getResponse().getContentAsString()==null||"".equals(result.getResponse().getContentAsString())){
            meteisnull = "success";
        }
        Assert.assertEquals("success", meteisnull);
    }

    @Test
    public void fetchMetaCacheHandlerFail() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(metaParam);

        PowerMockito.when(fetchMetaCacheHandler.handle(metaParam)).thenReturn(null);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/Diffservice/fetchMeta")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();
        System.out.println("Fail: "+result);
        String meteisnull = "";
        if ("".equals(result.getResponse().getContentAsString()))
        {
            meteisnull = "error";

        }
        Assert.assertEquals("error", meteisnull);
    }
}
