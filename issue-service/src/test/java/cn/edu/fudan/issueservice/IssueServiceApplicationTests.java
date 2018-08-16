package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.RawIssue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IssueServiceApplicationTests {

	private RawIssueDao rawIssueDao;

	@Autowired
	public void setRawIssueDao(RawIssueDao rawIssueDao) {
		this.rawIssueDao = rawIssueDao;
	}

	@Test
	public void testBatchUpdate(){

		List<RawIssue> list = new ArrayList<>();
		RawIssue rawIssue = new RawIssue();
		//45fca30a-db3a-43af-ae2a-94c7131184af
		rawIssue.setIssue_id("45fca30a-db3a-43af-ae2a-94c7131184af");
		rawIssue.setUuid("83795647-5813-4fd6-befd-bb7069613982");

		RawIssue rawIssue2 = new RawIssue();
		//b8fc7c9a-03b4-4d8b-8b34-ec8431bf8241
		rawIssue2.setIssue_id("b8fc7c9a-03b4-4d8b-8b34-ec8431bf8241");
		rawIssue2.setUuid("042caade-00de-45bc-8c19-01ef7e293930");

		list.add(rawIssue);
		list.add(rawIssue2);
		rawIssueDao.batchUpdateIssueId(list);

		System.out.println("success");
	}

}
