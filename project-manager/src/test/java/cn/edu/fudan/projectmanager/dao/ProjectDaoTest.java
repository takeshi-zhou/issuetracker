package cn.edu.fudan.projectmanager.dao;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.tool.MockTestConnection;
import cn.edu.fudan.projectmanager.tool.TestDataMaker;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectDaoTest extends ProjectManagerApplicationTests {

    @Autowired
    ProjectDao projectDao;

    Project project;
    Project projectUpdate;
    TestDataMaker testDataMaker;

    public static MockTestConnection mockTestConnection;

    @BeforeClass
    public static void setupConnection() throws Exception {
        mockTestConnection = new MockTestConnection();
        mockTestConnection.setupCoon();
    }


    @Before
    public void setup() throws Exception {

        project = testDataMaker.projectMakerPro2();

        projectUpdate = testDataMaker.projectMakerPro1();

        String sql = "insert into project values(\"pro1\",\"XChart\",\n" +
                "\"https://github.com/knowm/XChart\",\n" +
                "\"Java\",\"git\",\"1\",\"Downloaded\",\n" +
                "\"Scanned\",\"2018-07-24 08:33:40\",\n" +
                "\"2018-09-03 14:15:21\",\n" +
                "\"XChart is a light-weight Java library for plotting data.\",\n" +
                "\"cf91a362-ab35-11e8-8a7c-d067e5ea858d\")";
        Statement stmt = mockTestConnection.getConn().createStatement();//创建一个Statement对象
        stmt.executeUpdate(sql);//执行sql语句
        System.out.println("finish mocking");

    }

    @Test
    @Transactional
    public void addOneProject() {
        projectDao.addOneProject(project);

    }

    @Test
    public void getProjectByAccountId() {
        List<Project> list = projectDao.getProjectByAccountId("1");
        for (Project project : list) {
            System.out.println(project.getName() + "  " + project.getUrl());
        }
    }

    @Test
    public void getProjectByID() {
        Project project1 = projectDao.getProjectByID("pro1");
        System.out.println(project1.getAccount_id() + "  " + project1.getUrl());

    }

    @Test
    public void hasBeenAdded() {
        Boolean state1 = projectDao.hasBeenAdded("1", "https://github.com/knowm/XChart", "bug");
        System.out.println(state1);

        Boolean state2 = projectDao.hasBeenAdded("1", "https://github.com/mockito/mockito", "bug");
        System.out.println(state2);

    }

    @Test
    public void updateProjectStatus() {
        projectDao.updateProjectStatus(projectUpdate);
    }

    @Test
    public void remove() {
        projectDao.remove("pro1");
    }

    @Test
    public void getProjectNameById() {
        String projectName = projectDao.getProjectNameById("pro1");
        System.out.println(projectName);
    }

    @Test
    public void getRepoPath() {
        String repoPath = projectDao.getRepoPath("pro1");
        System.out.println(repoPath);
    }

    @Test
    public void getRepoId() {
        String repoId = projectDao.getRepoId("pro1");
        System.out.println(repoId);
    }

    @Test
    public void getProjectByKeyWordAndAccountId() {
        List<Project> projects = projectDao.getProjectByKeyWordAndAccountId("1", "x");
        System.out.println(projects);
        for (Project project : projects) {
            System.out.println(project.getUuid());
        }
    }

    @After
    public void cleanData() throws Exception {
        String sql = "delete from project where uuid='pro1'";
        Statement stmt = mockTestConnection.getConn().createStatement();//创建一个Statement对象
        stmt.executeUpdate(sql);//执行sql语句
        System.out.println("finish cleaning");
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        mockTestConnection.closeCoon();
        System.out.println("关闭数据库成功");
    }
}