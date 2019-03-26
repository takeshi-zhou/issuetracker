package cn.edu.fudan.recommendation.controller;

import cn.edu.fudan.recommendation.BugBugRecommendationApplicationTests;
import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.domain.ResponseBean;
import cn.edu.fudan.recommendation.service.RecommendationService;
import cn.edu.fudan.recommendation.service.impl.RecommendationServiceImpl;
import cn.edu.fudan.recommendation.tool.TestDataMaker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.alibaba.fastjson.JSONObject;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@PrepareForTest({RecommendationController.class, RecommendationService.class, RecommendationServiceImpl.class})
@WebAppConfiguration
public class BugRecommendationControllerTest extends BugBugRecommendationApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    RecommendationService recommendationService;

    @Autowired
    @InjectMocks
    RecommendationController recommendationController;

    private MockMvc mockMvc;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    private ResponseBean responseBean;
    private BugRecommendation recommendation;
    ObjectMapper mapper;

    @Before
    public void setupMockMvcAndData() throws Exception{
        recommendationService = Mockito.mock(RecommendationService.class);
        mapper = new ObjectMapper();
        MemberModifier.field(RecommendationController.class, "recommendationService").set(recommendationController,recommendationService);
        //setupMockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

        responseBean = new ResponseBean();
        recommendation = TestDataMaker.recommendationMaker();
    }

    @Test
    public void addBugRecommendationTestSuccess() throws Exception{
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(recommendation);
        doNothing().when(recommendationService).addBugRecommendation(any(BugRecommendation.class));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(
                "/bugRecommendation/add-bug-recommendation")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();
        ResponseBean responseBeanResult = JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
        Assert.assertEquals(200,responseBeanResult.getCode());
    }

    @Test
    public void addBugRecommendationTestFail() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(recommendation);
        PowerMockito.when(recommendationService, "addBugRecommendation", any(BugRecommendation.class)).thenThrow(new RuntimeException());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(
                "/bugRecommendation/add-bug-recommendation")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session)
        ).andReturn();
        ResponseBean responseBeanResult = JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
        Assert.assertEquals(401, responseBeanResult.getCode());
    }

//    @Test
//    @Ignore
//    public void getBugRecommendationTestSuccess() throws Exception{
//        String type = "SFA1";
//        PowerMockito.when(recommendationService.getRecommendationsByType(type)).thenReturn(recommendation);
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/bugRecommendation/get-bug-recommendation")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .param("type", type)
//                .session(session)
//        ).andReturn();
//        ResponseBean responseBeanResult = JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
//        Assert.assertEquals(200,responseBeanResult.getCode());
//    }
}
