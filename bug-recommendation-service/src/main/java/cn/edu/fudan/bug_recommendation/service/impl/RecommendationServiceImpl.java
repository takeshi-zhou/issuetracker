package cn.edu.fudan.bug_recommendation.service.impl;
import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import cn.edu.fudan.bug_recommendation.service.GetCode;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
import cn.edu.fudan.bug_recommendation.service.FunctionSimilarity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private RecommendationDao recommendationDao;
    private FunctionSimilarity functionSimilarity;
    private GetCode getCode;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setGetCode(GetCode getCode){this.getCode=getCode;}

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
        log.info("use getRecommendationsByType!");
        return result;
    }

    @Override
    public Object getBugRecommendationOrderBySimilarity(String type, Integer page, Integer size, String repoId, String commit_id,String location,Integer start_line,Integer end_line){
        //在数据库设置两个包
        log.info("use getBugRecommendationOrderBySimilarity!");
        String selectCode = stringRedisTemplate.opsForValue().get("code:"+type+repoId+location);
        if(selectCode==null){
            selectCode = getCode.getCode(repoId,commit_id,location);
            stringRedisTemplate.opsForValue().set("code:"+type+repoId+location,selectCode,300, TimeUnit.SECONDS);
        }else {
            stringRedisTemplate.opsForValue().set("code:"+type+repoId+location,selectCode,300, TimeUnit.SECONDS);
        }
        //根据type,repoId,location在redis里查找有没有相应的代码
        //有就取出来；没有就访问code接口，然后把代码放redis里，并设置过期时间
        //有了代码之后，根据type,code的hash在redis里查找有没有排序
        //有就直接拿，没有就进行计算，并把计算结果放redis里，设置过期时间
        //redis里没有code
        List<Recommendation> resultlist = new ArrayList<>();
        int start = (page - 1) * size;
        int end = start+size-1;
        List<String> sortList = stringRedisTemplate.opsForList().range("sort:"+type+repoId+location,start,end);
        if(sortList!=null && sortList.size()!=0){
            for(String sort:sortList){
                Recommendation reco = JSON.parseObject(sort,Recommendation.class);
                resultlist.add(reco);
            }
            return resultlist;
        }else{
            //redis里没有排序结果
            Map<String, Object> param = new HashMap<>();
            int count = recommendationDao.getRecommendationsByTypeCount(type);
            log.info("recos by same type -> count={}",count);
            //redis插入所有行
            param.put("type",type);
            param.put("size",count);
            param.put("start",0);
            List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(param);
            //对所有查询到的reco按相似度进行比较
            TreeMap<Float,Recommendation> treeMap = new TreeMap<>(new Comparator<Float>() {
                @Override
                public int compare(Float o1, Float o2) {
                    return o2.compareTo(o1);
                }
            });
            for(Recommendation reco:recommendationsList){
                Float simi = functionSimilarity.getFunctionSimilarity(getPrevBugContent(start_line,end_line,selectCode),getPrevBugContent(reco.getStart_line(),reco.getEnd_line(),reco.getPrev_code()));
                treeMap.put(simi,reco);
            }
            List<Float> simiList = new ArrayList<>(treeMap.keySet());
            for(Float f:simiList){
                System.out.println("simi: "+f);
            }
            //为什么少了呢，treemap的key不能重复
            //实际的排序条数少于count
            int factCount = treeMap.size();
            log.info("factCount -> factCount={}",factCount);
            stringRedisTemplate.opsForValue().set("sort:count:"+type+repoId+location,Integer.toString(factCount));
            List<Recommendation> valuelist = new ArrayList<>(treeMap.values());
            for(int i=start;i<=end&&i<valuelist.size();i++){
                if(start>factCount){
                    return new ArrayList<Recommendation>(0);
                }
                resultlist.add(valuelist.get(i));
            }

            for(Recommendation reco:valuelist){
                try {
                    //在key里面设置，那么如何初始化key
                    stringRedisTemplate.opsForList().rightPush("sort:"+type+repoId+location,JSON.toJSONString(reco));
                    //stringRedisTemplate.opsForList().set("sort:"+type+repoId+location,rediscnt,JSON.toJSONString(reco));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //stringRedisTemplate.opsForList().set("sort:"+type+repoId+location,0,JSON.toJSONString(valuelist));
            return resultlist;
        }

    }
//@Override
//public Object getBugRecommendationOrderBySimilarity(String type, Integer page, Integer size, String codeUrl,Integer start_line,Integer end_line){
//    //在数据库设置两个包
//    String selectCode = stringRedisTemplate.opsForValue().get(type+repoId+location);
//    if(selectCode==null){
//        selectCode = getCode.getCode(repoId,commit_id,location);
//        stringRedisTemplate.opsForValue().set(type+repoId+location,selectCode,300, TimeUnit.SECONDS);
//    }else {
//        stringRedisTemplate.opsForValue().set(type+repoId+location,selectCode,300, TimeUnit.SECONDS);
//    }
//    //根据type,repoId,location在redis里查找有没有相应的代码
//    //有就取出来；没有就访问code接口，然后把代码放redis里，并设置过期时间
//    //有了代码之后，根据type,code的hash在redis里查找有没有排序
//    //有就直接拿，没有就进行计算，并把计算结果放redis里，设置过期时间
//    //redis里没有code
//    List<Recommendation> resultlist = new ArrayList<>();
//    int start = (page - 1) * size;
//    int end = start+size-1;
//    List<String> sortList = stringRedisTemplate.opsForList().range(type+repoId+location,start,end);
//    if(sortList!=null && sortList.size()!=0){
//        for(String sort:sortList){
//            Recommendation reco = JSON.parseObject(sort,Recommendation.class);
//            resultlist.add(reco);
//        }
//        return resultlist;
//    }else{
//        //redis里没有排序结果
//        Map<String, Object> param = new HashMap<>();
//        int count = recommendationDao.getRecommendationsByTypeCount(type);
//        param.put("type",type);
//        param.put("size",count);
//        param.put("start", 0);
//        List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(param);
//        //对所有查询到的reco按相似度进行比较
//        TreeMap<Float,Recommendation> treeMap = new TreeMap<>(new Comparator<Float>() {
//            @Override
//            public int compare(Float o1, Float o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        for(Recommendation reco:recommendationsList){
//            Float simi = functionSimilarity.getFunctionSimilarity(getPrevBugContent(start_line,end_line,selectCode),getPrevBugContent(reco.getStart_line(),reco.getEnd_line(),reco.getPrev_code()));
//            treeMap.put(simi,reco);
//        }
//        List<Recommendation> valuelist = new ArrayList<>(treeMap.values());
//        for(int i=start;i<=end;i++){
//            resultlist.add(valuelist.get(i));
//        }
//        stringRedisTemplate.opsForList().set(type+repoId+location,0,JSON.toJSONString(valuelist));
//        return resultlist;
//    }
//
//}

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
    //对于currcode拿到所有方法名相同的方法并累加
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
            if(similarityPrev>0.998 && similarityCurr>0.998){
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
            if(sim>=0.999){
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