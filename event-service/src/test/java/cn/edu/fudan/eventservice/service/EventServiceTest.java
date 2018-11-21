package cn.edu.fudan.eventservice.service;

import cn.edu.fudan.eventservice.EventServiceApplicationTests;
import cn.edu.fudan.eventservice.dao.EventDao;
import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.EventType;
import cn.edu.fudan.eventservice.service.impl.EventServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@PrepareForTest({EventService.class, EventServiceImpl.class,EventDao.class,StringRedisTemplate.class})
public class EventServiceTest extends EventServiceApplicationTests {

    @Autowired
    private EventService eventService;

    private EventDao eventDao;

    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations valueOperations;

    private List<Event> testEvents;

    @Before
    public void setUp() throws IllegalAccessException {
        eventDao= Mockito.mock(EventDao.class);
        stringRedisTemplate=Mockito.mock(StringRedisTemplate.class);
        valueOperations=Mockito.mock(ValueOperations.class);
        MemberModifier.field(EventServiceImpl.class,"eventDao").set(eventService,eventDao);
        MemberModifier.field(EventServiceImpl.class,"stringRedisTemplate").set(eventService,stringRedisTemplate);

        //准备测试数据
        testEvents=new ArrayList<>();
        testEvents.add(new Event("1","bug", EventType.NEW_BUG,"BIG_METHOD","2222","Jack","1",new Date()));
        testEvents.add(new Event("2","bug", EventType.ELIMINATE_BUG,"11","2222","Smith","1",new Date()));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void addEventsSuccess() throws Exception {
        PowerMockito.doNothing().when(eventDao,"addEvents",ArgumentMatchers.anyIterable());
        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        PowerMockito.doNothing().when(valueOperations,"set",ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
        eventService.addEvents(testEvents);
        Mockito.verify(eventDao, Mockito.atLeastOnce()).addEvents(testEvents);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addNullEvents() throws Exception {
        PowerMockito.doNothing().when(eventDao,"addEvents",ArgumentMatchers.anyIterable());
        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        PowerMockito.doNothing().when(valueOperations,"set",ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
        eventService.addEvents(null);
        Mockito.verify(eventDao, Mockito.never()).addEvents(testEvents);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addEmptyEvents() throws Exception {
        PowerMockito.doNothing().when(eventDao,"addEvents",ArgumentMatchers.anyIterable());
        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        PowerMockito.doNothing().when(valueOperations,"set",ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
        List<Event> events=new ArrayList<>();
        eventService.addEvents(events);
        Mockito.verify(eventDao, Mockito.never()).addEvents(testEvents);
    }

    @Test
    public void deleteEvents() throws Exception {
        String repoId="1111";
        String category="bug";
        PowerMockito.doNothing().when(eventDao,"deleteEventByRepoIdAndCategory",ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
        eventService.deleteEvents(repoId,category);
        Mockito.verify(eventDao, Mockito.atLeastOnce()).deleteEventByRepoIdAndCategory(category,repoId);
    }

    @Test
    public void deleteEventsWithNullParam() throws Exception {
        String repoId=null;
        String category="bug";
        PowerMockito.doNothing().when(eventDao,"deleteEventByRepoIdAndCategory",ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
        eventService.deleteEvents(repoId,category);
        Mockito.verify(eventDao, Mockito.never()).deleteEventByRepoIdAndCategory(category,repoId);
    }

    @Test
    public void getBugEvents(){

    }

    public void getCloneEvents(){

    }

    @Test
    public void hasNewEvents(){

    }

}
