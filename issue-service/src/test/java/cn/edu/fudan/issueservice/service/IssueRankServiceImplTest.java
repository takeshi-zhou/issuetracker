/**
 * @description:
 * @author: fancying
 * @create: 2019-04-10 16:09
 **/
package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.service.impl.BaseMappingServiceImpl;
import cn.edu.fudan.issueservice.service.impl.IssueRankServiceImpl;
import cn.edu.fudan.issueservice.service.impl.IssueServiceImpl;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@PrepareForTest({IssueService.class, IssueServiceImpl.class, IssueDao.class, RawIssueDao.class, RestTemplate.class, StringRedisTemplate.class, HashOperations.class, ResponseEntity.class, ListOperations.class, BaseMappingServiceImpl.class})
public class IssueRankServiceImplTest {


    @Test
    public void test() {

    }

}