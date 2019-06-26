package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import lombok.extern.slf4j.Slf4j;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
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
        result.put("modifyTotalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("modifyTotalCount", count);
        List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(param);
        result.put("recommendationsList",recommendationsList);
        return result;
    }

    @Override
    //对于prevcode只比较startline到endline就知道出现的问题是否一样
    public String getPrevBugContent(Integer startLine,Integer endLine,String sFile){
        StringReader sReader = new StringReader(sFile);
        BufferedReader bReader = new BufferedReader(sReader);
        StringBuilder sb = new StringBuilder();
        String txt ="";
        try {
            int i=0;
            while((txt = bReader.readLine())!=null) {
                i++;
                if(i>=startLine && i<=endLine) {
                    sb.append(txt);
                }
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return sb.toString();
    }
    @Override
    public String getCurrBugContent(Recommendation recommendation){
        StringBuilder sb=new StringBuilder();
        CompilationUnit compilationUnit = JavaParser.parse(recommendation.getCurr_code());
        List<MethodDeclaration> methods = compilationUnit.getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration mtn : methods) {
            if (mtn.getName().getIdentifier().equals(recommendation.getMethod_name())) {
                sb.append(mtn.getBody());
            }
        }
        return sb.toString();
    }
    @Override
    public boolean isBugRecommendationExist(Recommendation recommendation){
        String type = recommendation.getType();
        String filename = recommendation.getFilename();
        List<Recommendation> filegroup = recommendationDao.getRecommendationsSameTypeFile(type,filename);
        float similarityPrev=0;
        float similarityCurr=0;
        for(Recommendation reco:filegroup){
            similarityPrev = functionSimilarity.getFunctionSimilarity(getPrevBugContent(recommendation.getStart_line(),recommendation.getEnd_line(),recommendation.getPrev_code()),getPrevBugContent(reco.getStart_line(),reco.getEnd_line(),reco.getPrev_code()));
            similarityCurr = functionSimilarity.getFunctionSimilarity(getCurrBugContent(recommendation),getCurrBugContent(reco));
            if(similarityPrev>0.99 && similarityCurr>0.99){
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
        if(recommendation.getPrev_code()!=null){
            float sim = functionSimilarity.getFunctionSimilarity(recommendation.getPrev_code(),recommendation.getCurr_code());
            if(sim>=0.995){
                throw new Exception("The code before and after this record has not been modified.");
            }
        }else {
            throw new Exception("The Prev_code is null.");
        }

        boolean isExist = isBugRecommendationExist(recommendation);
        if(isExist){
            System.out.println("SameReco had add one");
            throw new Exception("SameReco had add one");
        }
        //recommendation.setAppear_num(recommendation.getAppear_num()+1);

        recommendation.setUuid(UUID.randomUUID().toString());


        recommendationDao.addBugRecommendation(recommendation);

    }
    @Override
    public void addUsefulCount(String uuid,Integer useful_count){
        recommendationDao.addUsefulCount(uuid,useful_count);
    }
    @Override
    public void deleteBugRecommendationByRepoId(String repoId){
        log.info("start to delete issue -> repoId={}",repoId);
        recommendationDao.deleteBugRecommendationByRepoId(repoId);
        log.info("issue delete success!");
    }

}