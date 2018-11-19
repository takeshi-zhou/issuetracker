package cn.edu.fudan.eventservice.controller;

import cn.edu.fudan.eventservice.EventServiceApplicationTests;
import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.EventType;
import cn.edu.fudan.eventservice.domain.ResponseBean;
import cn.edu.fudan.eventservice.service.EventService;
import cn.edu.fudan.eventservice.service.impl.EventServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doNothing;

/**
 * @author WZY
 * @version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    public void addEventsSuccess() throws Exception {
        List<Event> testEvents=new ArrayList<>();
        testEvents.add(new Event("1","bug", EventType.NEW_BUG,"BIG_METHOD","2222","Jack","1",new Date()));
        testEvents.add(new Event("2","clone", EventType.NEW_CLONE_INSTANCE,"11","2222","Smith","1",new Date()));
        String requestJson=mapper.writer().withDefaultPrettyPrinter().writeValueAsString(testEvents);

        doNothing().when(eventService).addEvents(testEvents);

        ResponseBean expected=new ResponseBean(200,"event add success!",null);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
        ).andReturn();
        ResponseBean testResult= JSONObject.parseObject(result.getResponse().getContentAsString(), ResponseBean.class);
        Assert.assertEquals(expected.getCode(),testResult.getCode());
    }

    public void addEventsFailed(){

    }
}
