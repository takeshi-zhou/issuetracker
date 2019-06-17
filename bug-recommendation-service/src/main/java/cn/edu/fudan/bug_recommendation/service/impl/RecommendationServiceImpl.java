package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private RecommendationDao recommendationDao;
    private FunctionSimilarity functionSimilarity;

    @Autowired
    public void setRecommendationDao(RecommendationDao recommendationDao){
        this.recommendationDao=recommendationDao;
    }

    @Autowired
    public void setFunctionSimilarity(FunctionSimilarity functionSimilarity){this.functionSimilarity=functionSimilarity;}

    public boolean isLocationExist(String location){
        return recommendationDao.isLocationExist(location);
    }

    public boolean isStart_lineExist(Integer start_line){
        return recommendationDao.isStart_lineExist(start_line);

    }

    public boolean isTypeExist(Map<String, Object> map){
        return recommendationDao.isTypeExist(map);
    }

    public boolean isEnd_lineExist(Integer end_line){
        return recommendationDao.isEnd_lineExist(end_line);
    }

    public boolean isBuglinesExit(String bug_lines){
        return recommendationDao.isBuglinesExist(bug_lines);
    }

//    public List<Recommendation> getRecommendationsByType(String type){
//        List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(type);
//        return recommendationsList;
//    }
    @Override
    public Object getRecommendationsByType(String type,Integer page,Integer size){
        //获取此type的Recommendations的条数，计算每一页的list
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("type",type);
        param.put("size",size);
        int count = recommendationDao.getRecommendationsByTypeCount(type);
        param.put("start", (page - 1) * size);
        result.put("totalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("totalCount", count);
        List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(param);
        result.put("recommendationsList",recommendationsList);
        return result;
    }

    @Override
    public boolean isBugRecommendationExist(String codeprev,String codecurr,String filename,String type){
        List<Recommendation> filegroup = recommendationDao.getRecommendationsSameTypeFile(type,filename);
        float similarityPrev=0;
        float similarityCurr=0;
        for(Recommendation reco:filegroup){
            similarityPrev = functionSimilarity.getFunctionSimilarity(codeprev,reco.getPrev_code());
            similarityCurr = functionSimilarity.getFunctionSimilarity(codecurr,reco.getCurr_code());
            if(similarityPrev>0.9 && similarityCurr>0.9){
                int tmp = reco.getAppear_num()+1;
                recommendationDao.updateRecommendationsAppearNum(tmp,reco.getUuid());
                System.out.println("tmp: "+tmp);
                //reco.setAppear_num(tmp);
                return true;
            }
        }
        return false;
    }



    public void addBugRecommendation(Recommendation recommendation) throws Exception{
        if (recommendation.getType()==null ||
                recommendation.getLocation()==null ||
                recommendation.getStart_line()==null || recommendation.getEnd_line()==null || recommendation.getCurr_commitid()==null ||
                recommendation.getNext_commitid()==null||recommendation.getAppear_num()==null)
        {
            throw new Exception("param loss");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("type",recommendation.getType());
        param.put("start",0);
        param.put("size",recommendationDao.getRecommendationsByTypeCount(recommendation.getType()));
//        if (isLocationExist(recommendation.getLocation()) && isTypeExist(param) && isStart_lineExist(recommendation.getStart_line())
//               && isEnd_lineExist(recommendation.getEnd_line()) && isBuglinesExit(recommendation.getBug_lines()))
//           throw new RuntimeException("This error message already exists");
        float sim = functionSimilarity.getFunctionSimilarity(recommendation.getPrev_code(),recommendation.getCurr_code());
        if(sim>=0.95){
            throw new Exception("The code before and after this record has not been modified.");
        }
        boolean istrue = isBugRecommendationExist(recommendation.getPrev_code(),recommendation.getCurr_code(),recommendation.getFilename(),recommendation.getType());
        if(istrue){
            System.out.println("SameReco had add one");
            throw new Exception("SameReco had add one");
        }
        //recommendation.setAppear_num(recommendation.getAppear_num()+1);

        recommendation.setUuid(UUID.randomUUID().toString());


        recommendationDao.addBugRecommendation(recommendation);

    }


}