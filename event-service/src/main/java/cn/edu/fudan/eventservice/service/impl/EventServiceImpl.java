package cn.edu.fudan.eventservice.service.impl;

import cn.edu.fudan.eventservice.dao.EventDao;
import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.EventType;
import cn.edu.fudan.eventservice.domain.EventsDto;
import cn.edu.fudan.eventservice.service.EventService;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service
public class EventServiceImpl implements EventService {


    @Value("${inner.service.path}")
    private String innerServicePath;

    private EventDao eventDao;
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private StringRedisTemplate stringRedisTemplate;

    public EventServiceImpl(EventDao eventDao,
                            RestTemplate restTemplate,
                            HttpHeaders httpHeaders,
                            StringRedisTemplate stringRedisTemplate) {
        this.eventDao = eventDao;
        this.restTemplate=restTemplate;
        this.httpHeaders=httpHeaders;
        this.stringRedisTemplate=stringRedisTemplate;
    }

    @Override
    public void addEvents(List<Event> events) {
        eventDao.addEvents(events);
    }

    @Override
    public void deleteEvents(String repo_id, String category) {
        if(category.equals("bug")){
            eventDao.deleteEventByRepoIdAndCategory(EventType.bugEventTypeList,repo_id);
        }else if(category.equals("clone")){
            eventDao.deleteEventByRepoIdAndCategory(EventType.cloneEventTypeList,repo_id);
        }
    }

    @Override
    public Object getCurrentEvents(String userToken,String category) {

        Map<String,Object> result=new HashMap<>();
        HttpEntity<String> httpEntity=new HttpEntity<>(httpHeaders);
        Map<String,String> urlParameters=new HashMap<>();
        urlParameters.put("userToken",userToken);
        String accountId=restTemplate.exchange(innerServicePath+"/user/accountId?userToken={userToken}", HttpMethod.GET,httpEntity,String.class,urlParameters).getBody();
        urlParameters.put("accountId",accountId);
        urlParameters.put("type",category);
        JSONArray repoIds=restTemplate.exchange(innerServicePath+"/inner/project/repo-ids?account_id={accountId}&type={type}",HttpMethod.GET,httpEntity,JSONArray.class,urlParameters).getBody();

        if(repoIds==null||repoIds.isEmpty()){
            if(category.equals("bug")){
                result.put("newBug",new EventsDto(false,Collections.emptyList()));
                result.put("eliminateBug",new EventsDto(false,Collections.emptyList()));
            }else{
                result.put("newCloneClass",new EventsDto(false,Collections.emptyList()));
                result.put("removeCloneClass",new EventsDto(false,Collections.emptyList()));
                result.put("newCloneInstance",new EventsDto(false,Collections.emptyList()));
                result.put("removeCloneInstance",new EventsDto(false,Collections.emptyList()));
            }
            result.put("hasNew",false);
            return result;
        }


        List<String> repoIdList=repoIds.toJavaList(String.class);
        if(category.equals("bug")){
            List<Event> newBugList=eventDao.getRecentEventsByEventType(repoIdList, EventType.NEW_BUG);
            List<Event> eliminateBugList=eventDao.getRecentEventsByEventType(repoIdList,EventType.ELIMINATE_BUG);
            String newBugKey="event:"+accountId+":new_bug";
            boolean newBugChanged=eventChangeUpdate(newBugKey,newBugList);
            result.put("newBug",new EventsDto(newBugChanged,newBugList));
            String eliminateBugKey="event:"+accountId+":eliminate_bug";
            boolean eliminateBugChanged=eventChangeUpdate(eliminateBugKey,eliminateBugList);
            result.put("eliminateBug",new EventsDto(eliminateBugChanged,eliminateBugList));
            result.put("hasNew",newBugChanged||eliminateBugChanged);
        }else if(category.equals("clone")){
            List<Event> newCloneClassList=eventDao.getRecentEventsByEventType(repoIdList, EventType.NEW_CLONE_CLASS);
            List<Event> removeCloneClassList=eventDao.getRecentEventsByEventType(repoIdList,EventType.REMOVE_CLONE_CLASS);
            List<Event> newCloneInstanceList=eventDao.getRecentEventsByEventType(repoIdList, EventType.NEW_CLONE_INSTANCE);
            List<Event> removeCloneInstanceList=eventDao.getRecentEventsByEventType(repoIdList,EventType.REMOVE_CLONE_INSTANCE);
            String newCloneClassKey="event:"+accountId+":new_clone_class";
            boolean newCloneClassChanged=eventChangeUpdate(newCloneClassKey,newCloneClassList);
            result.put("newCloneClass",new EventsDto(newCloneClassChanged,newCloneClassList));

            String removeCloneClassKey="event:"+accountId+":remove_clone_class";
            boolean removeCloneClassChanged=eventChangeUpdate(removeCloneClassKey,removeCloneClassList);
            result.put("removeCloneClass",new EventsDto(removeCloneClassChanged,removeCloneClassList));

            String newCloneInstanceKey="event:"+accountId+":new_clone_instance";
            boolean newCloneInstanceChanged=eventChangeUpdate(newCloneInstanceKey,newCloneInstanceList);
            result.put("newCloneInstance",new EventsDto(newCloneInstanceChanged,newCloneInstanceList));

            String removeCloneInstanceKey="event:"+accountId+":remove_clone_instance";
            boolean removeCloneInstanceChanged=eventChangeUpdate(removeCloneInstanceKey,removeCloneInstanceList);
            result.put("removeCloneInstance",new EventsDto(removeCloneInstanceChanged,removeCloneInstanceList));

            result.put("hasNew",newCloneClassChanged||removeCloneClassChanged||newCloneInstanceChanged||removeCloneInstanceChanged);
        }


        return result;
    }

    private boolean eventChangeUpdate(String key,List<Event> list){
        boolean changed=false;
        Boolean hasKey=stringRedisTemplate.hasKey(key);
        if(hasKey!=null&&hasKey){
            int newBugSize=Integer.parseInt(stringRedisTemplate.opsForValue().get(key));
            if(list.size()!=newBugSize){
                stringRedisTemplate.opsForValue().set(key,String.valueOf(list.size()));
                changed=true;
            }
        }else{
            stringRedisTemplate.opsForValue().set(key,String.valueOf(list.size()));
            changed=true;
        }
        return  changed;
    }
}
