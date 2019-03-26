package cn.edu.fudan.recommendation.service;

import cn.edu.fudan.recommendation.BugBugRecommendationApplicationTests;
import cn.edu.fudan.recommendation.dao.RecommendationDao;
import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.domain.ResponseBean;
import cn.edu.fudan.recommendation.service.impl.RecommendationServiceImpl;
import cn.edu.fudan.recommendation.tool.TestDataMaker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@PrepareForTest({RecommendationService.class, RecommendationServiceImpl.class, RecommendationDao.class})
public class BugRecommendationServiceTest extends BugBugRecommendationApplicationTests {

    @Mock
    private RecommendationDao recommendationDao;

    @Autowired
    @InjectMocks
    private RecommendationService recommendationService = new RecommendationServiceImpl();

    private ResponseBean responseBean;
    private BugRecommendation recommendation;

    @Before
    public void setupData() throws Exception{
        responseBean = new ResponseBean();
        recommendation = TestDataMaker.recommendationMaker();
        MemberModifier.field(RecommendationServiceImpl.class,"recommendationDao").set(recommendationService,recommendationDao);
        System.out.println("finish mocking");
    }

    @Test
    public void addBugRecommendationTest_Null(){
        recommendation.setType(null);
        try {
            recommendationService.addBugRecommendation(recommendation);
        }catch (Exception e){
            Assert.assertEquals("param loss",e.getMessage());
        }
    }

    @Test
    public void addBugRecommendationTest_TypeLocationIsExist(){
        PowerMockito.when(recommendationService.isTypeExist(recommendation.getType())).thenReturn(true);
        PowerMockito.when(recommendationService.isLocationExist(recommendation.getLocation())).thenReturn(true);
        try {
            recommendationService.addBugRecommendation(recommendation);
        }catch (Exception e){
            Assert.assertEquals("This error message already exists",e.getMessage());
        }
    }

    @Test
    public void addBugRecommendationTest_Success(){
        PowerMockito.when(recommendationService.isTypeExist(recommendation.getType())).thenReturn(false);
        PowerMockito.when(recommendationService.isLocationExist(recommendation.getLocation())).thenReturn(false);
        doNothing().when(recommendationDao).addBugRecommendation(recommendation);
        try {
            recommendationService.addBugRecommendation(recommendation);
        }catch (Exception e){
        }
        verify(recommendationDao,Mockito.atLeast(1)).addBugRecommendation(recommendation);
    }

    @Test
    public void isLocationExistTest(){
        /*
        * location已存在时
        */
        String location = "d:/a/b";
        PowerMockito.when(recommendationDao.isLocationExist(location)).thenReturn(true);
        Boolean result = recommendationService.isLocationExist(location);
        Assert.assertEquals(true,result);
        /*
         * location不存在时
         */
        PowerMockito.when(recommendationDao.isLocationExist(location)).thenReturn(false);
        result = recommendationService.isLocationExist(location);
        Assert.assertEquals(false,result);
    }

    @Test
    public void isTypeExistTest(){
        /*
            Type 已存在时
         */
        String type = "SFAF12";
        PowerMockito.when(recommendationDao.isTypeExist(type)).thenReturn(true);
        Boolean result = recommendationService.isTypeExist(type);
        Assert.assertEquals(true,result);

        /*
            Type 不存在时
         */
        PowerMockito.when(recommendationDao.isTypeExist(type)).thenReturn(false);
        result = recommendationService.isTypeExist(type);
        Assert.assertEquals(false,result);
    }
}
