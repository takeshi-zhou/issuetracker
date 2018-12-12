package cn.edu.fudan.eventservice.service;

import cn.edu.fudan.eventservice.EventServiceApplicationTests;
import cn.edu.fudan.eventservice.component.RestInterfaceManager;
import cn.edu.fudan.eventservice.dao.EventDao;
import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.EventType;
import cn.edu.fudan.eventservice.service.impl.EventServiceImpl;
import com.alibaba.fastjson.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author WZY
 * @version 1.0
 **/
@PrepareForTest({EventService.class, EventServiceImpl.class,EventDao.class,StringRedisTemplate.class})
public class EventServiceTest extends EventServiceApplicationTests {

    private static final String EVENT_HAS_NEW_KEY_PREFIX="event:has_new:";

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    private EventService eventService;

    private EventDao eventDao;

    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations valueOperations;

    private List<Event> testEvents;

    private JSONArray repoIds;

    private List<Event> newBugEvents;

    private List<Event> eliminatedBugEvents;

    private List<Event> newCloneClassEvents;

    private List<Event> removeCloneClassEvents;

    private List<Event> newCloneInstanceEvents;

    private List<Event> removeCloneInstanceEvents;


    @Before
    public void setUp() throws IllegalAccessException {
        eventDao= Mockito.mock(EventDao.class);
        stringRedisTemplate=Mockito.mock(StringRedisTemplate.class);
        valueOperations=Mockito.mock(ValueOperations.class);
        restInterfaceManager=Mockito.mock(RestInterfaceManager.class);
        MemberModifier.field(EventServiceImpl.class,"eventDao").set(eventService,eventDao);
        MemberModifier.field(EventServiceImpl.class,"stringRedisTemplate").set(eventService,stringRedisTemplate);
        MemberModifier.field(EventServiceImpl.class,"restInterfaceManager").set(eventService,restInterfaceManager);
        //准备测试数据
        testEvents=new ArrayList<>();
        testEvents.add(new Event("1","bug", EventType.NEW_BUG,"BIG_METHOD","2222","Jack","1",new Date()));
        testEvents.add(new Event("2","bug", EventType.ELIMINATE_BUG,"11","2222","Smith","1",new Date()));

        repoIds=new JSONArray();
        repoIds.add("111111111111111111111111111");
        repoIds.add("222222222222222222222222222");
        repoIds.add("333333333333333333333333333");

        newBugEvents=new ArrayList<>();
        newBugEvents.add(new Event("3","bug", EventType.NEW_BUG,"BIG_METHOD","2222","Jack","1",new Date()));
        newBugEvents.add(new Event("4","bug", EventType.NEW_BUG,"BIG_METHOD","2222","Jack","1",new Date()));

        eliminatedBugEvents=new ArrayList<>();
        eliminatedBugEvents.add(new Event("5","bug", EventType.ELIMINATE_BUG,"BIG_METHOD","2222","Jack","1",new Date()));
        eliminatedBugEvents.add(new Event("6","bug", EventType.ELIMINATE_BUG,"BIG_METHOD","2222","Jack","1",new Date()));

        newCloneClassEvents=new ArrayList<>();
        newCloneClassEvents.add(new Event("7","clone", EventType.NEW_CLONE_CLASS,"BIG_METHOD","2222","Jack","1",new Date()));
        newCloneClassEvents.add(new Event("8","clone", EventType.NEW_CLONE_CLASS,"BIG_METHOD","2222","Jack","1",new Date()));

        removeCloneClassEvents=new ArrayList<>();
        removeCloneClassEvents.add(new Event("9","clone", EventType.REMOVE_CLONE_CLASS,"BIG_METHOD","2222","Jack","1",new Date()));
        removeCloneClassEvents.add(new Event("10","clone", EventType.REMOVE_CLONE_CLASS,"BIG_METHOD","2222","Jack","1",new Date()));

        newCloneInstanceEvents=new ArrayList<>();
        newCloneInstanceEvents.add(new Event("11","clone", EventType.NEW_CLONE_INSTANCE,"BIG_METHOD","2222","Jack","1",new Date()));
        newCloneInstanceEvents.add(new Event("12","clone", EventType.NEW_CLONE_INSTANCE,"BIG_METHOD","2222","Jack","1",new Date()));

        removeCloneInstanceEvents=new ArrayList<>();
        removeCloneInstanceEvents.add(new Event("13","clone", EventType.REMOVE_CLONE_INSTANCE,"BIG_METHOD","2222","Jack","1",new Date()));
        removeCloneInstanceEvents.add(new Event("14","clone", EventType.REMOVE_CLONE_INSTANCE,"BIG_METHOD","2222","Jack","1",new Date()));
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
    @SuppressWarnings("unchecked")
    public void getBugEvents() throws Exception {
        String userToken="12222222222222";
        String accountId="1";
        String category="bug";
        Map<String,Object> expectedResult=new HashMap<>();
        expectedResult.put("newBug",newBugEvents);
        expectedResult.put("eliminateBug",eliminatedBugEvents);

        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(userToken,category)).thenReturn(repoIds);

        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.NEW_BUG))
                .thenReturn(newBugEvents);
        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.ELIMINATE_BUG))
                .thenReturn(eliminatedBugEvents);
        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        PowerMockito.doNothing()
                .when(valueOperations,"set",ArgumentMatchers.eq(EVENT_HAS_NEW_KEY_PREFIX+category+":"+ArgumentMatchers.anyString()),"false");

        Map<String,Object> realResult=(Map<String,Object>)eventService.getCurrentEvents(userToken,category);
        Assert.assertEquals(expectedResult.size(),realResult.size());
        eventsAssert(expectedResult,realResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCloneEvents() throws Exception {
        String userToken="12222222222222";
        String accountId="1";
        String category="clone";
        Map<String,Object> expectedResult=new HashMap<>();
        expectedResult.put("newCloneClass",newCloneClassEvents);
        expectedResult.put("removeCloneClass",removeCloneClassEvents);
        expectedResult.put("newCloneInstance",newCloneInstanceEvents);
        expectedResult.put("removeCloneInstance",removeCloneInstanceEvents);

        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(userToken,category)).thenReturn(repoIds);

        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.NEW_CLONE_CLASS))
                .thenReturn(newCloneClassEvents);
        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.REMOVE_CLONE_CLASS))
                .thenReturn(removeCloneClassEvents);
        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.NEW_CLONE_INSTANCE))
                .thenReturn(newCloneInstanceEvents);
        PowerMockito.when(eventDao.getRecentEventsByEventType(repoIds.toJavaList(String.class),EventType.REMOVE_CLONE_INSTANCE))
                .thenReturn(removeCloneInstanceEvents);
        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        PowerMockito.doNothing()
                .when(valueOperations,"set",ArgumentMatchers.eq(EVENT_HAS_NEW_KEY_PREFIX+category+":"+ArgumentMatchers.anyString()),"false");

        Map<String,Object> realResult=(Map<String,Object>)eventService.getCurrentEvents(userToken,category);

        eventsAssert(expectedResult,realResult);
    }

    @SuppressWarnings("unchecked")
    private void eventsAssert(Map<String,Object> expectedResult,Map<String,Object> realResult){
        Assert.assertEquals(expectedResult.size(),realResult.size());
        for(String key:realResult.keySet()){
            List<Event> realList=(List<Event>)realResult.get(key);
            List<Event> expectList=(List<Event>)expectedResult.get(key);
            Assert.assertEquals(realList.size(),expectList.size());
            for(int j=0;j<realList.size();j++){
                Assert.assertEquals(expectList.get(j).getId(),realList.get(j).getId());
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hasNewEvents() throws Exception {
        String userToken="12222222222222";
        String accountId="1";
        String category="bug";
        Map<String,Object> expectedResult=new HashMap<>();
        expectedResult.put("hasNew",true);

        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(userToken,category)).thenReturn(repoIds);

        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        PowerMockito
                .when(valueOperations,"get",ArgumentMatchers.eq(EVENT_HAS_NEW_KEY_PREFIX+category+":222222222222222222222222222"))
                .thenReturn("true");

        Map<String,Object> realResult=(Map<String,Object>)eventService.hasNewEvents(userToken,category);
        Assert.assertEquals(expectedResult.get("hasNew"),realResult.get("hasNew"));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void doNotHasNewEvents() throws Exception {
        String userToken="12222222222222";
        String accountId="1";
        String category="bug";
        Map<String,Object> expectedResult=new HashMap<>();
        expectedResult.put("hasNew",false);

        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(userToken,category)).thenReturn(repoIds);

        PowerMockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        PowerMockito
                .when(valueOperations,"get",ArgumentMatchers.anyString())
                .thenReturn("false");

        Map<String,Object> realResult=(Map<String,Object>)eventService.hasNewEvents(userToken,category);
        Assert.assertEquals(expectedResult.get("hasNew"),realResult.get("hasNew"));

    }

}
