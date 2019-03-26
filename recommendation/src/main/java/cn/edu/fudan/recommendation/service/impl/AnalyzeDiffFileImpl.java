package cn.edu.fudan.recommendation.service.impl;

import cn.edu.fudan.recommendation.service.AnalyzeDiffFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Service
public class AnalyzeDiffFileImpl implements AnalyzeDiffFile {
    //repopath前两段 + nextcommitid + gen + commitid + (Diff+repopath最后一段+.json)
    public JSONObject getDiffRange(String repopath,String nextcommitid,String commitid,String bug_line){
        String[] RepoArry = repopath.split("/");
        String DiffJsonPath = "";
        String javaName = RepoArry[RepoArry.length-1];
        for (int i = 0; i<2;i++){
            DiffJsonPath = DiffJsonPath + RepoArry[i];
            DiffJsonPath += "/";
        }

        DiffJsonPath = "diffpath/"+ DiffJsonPath + nextcommitid + "/" + "gen" + "/" + commitid + "/" + "Diff"+javaName+".json";
        FileSystemResource resource = new FileSystemResource(DiffJsonPath);
        String jsonString = "";
        JSONArray jsonArray = new JSONArray();
        try {
            File file = resource.getFile();
            jsonString = FileUtils.readFileToString(file);
            jsonArray = JSONArray.parseArray(jsonString);
            System.out.println("filecontent: "+ jsonArray);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        Integer start_line = new Integer("0");
        Integer end_line = new Integer("0");
        Integer nextstart_line = new Integer("0");
        Integer nextend_line = new Integer("0");
        String description = "";
        String bugline_one = bug_line.split(",")[0];
        Integer bug_lines = Integer.valueOf(bugline_one);

        for (int i = 0; i<jsonArray.size();i++){
            JSONObject item = JSONObject.parseObject(jsonArray.getString(i));
            String range = item.getString("range");
            if(item.getString("description").indexOf("delete")==-1) {
                //当前版本start-end-line
                String currRange = range.split("-")[0];
                String tem0 = currRange.split(",")[0];
                String tem00 = currRange.split(",")[1];
                Integer currStartLine = Integer.valueOf(tem0.substring(1, tem0.length()));
                Integer currEndLine = Integer.valueOf(tem00.substring(0, tem00.length() - 1));

                //下一版本start-end-line
                String nextRange = range.split("-")[1];
                String tem1 = nextRange.split(",")[0];
                String tem11 = nextRange.split(",")[1];
                Integer nextStartLine = Integer.valueOf(tem1.substring(1, tem1.length()));
                Integer nextEndLine = Integer.valueOf(tem11.substring(0, tem11.length() - 1));

                if (currStartLine < bug_lines && currEndLine > bug_lines) {
                    start_line = currStartLine;
                    end_line = currEndLine;
                    nextstart_line = nextStartLine;
                    nextend_line = nextEndLine;
                    description = item.getString("description");
                }
            }else {
                String pretem = range.split(",")[0];
                Integer currStartLine = Integer.valueOf(pretem.substring(1, pretem.length()));
                String endtem = range.split(",")[1];
                Integer currEndLine = Integer.valueOf(endtem.substring(0, endtem.length() - 1));

                if (currStartLine < bug_lines && currEndLine > bug_lines) {
                    start_line = currStartLine;
                    end_line = currEndLine;
                    description = item.getString("description");
                }
            }
        }
        if (start_line == 0 && end_line == 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = JSONObject.parseObject(jsonArray.getString(i));
                String range = item.getString("range");
                if (item.getString("description").indexOf("delete") == -1) {
                    //当前版本start-end-line
                    String currRange = range.split("-")[0];
                    String tem0 = currRange.split(",")[0];
                    String tem00 = currRange.split(",")[1];
                    Integer currStartLine = Integer.valueOf(tem0.substring(1, tem0.length()));
                    Integer currEndLine = Integer.valueOf(tem00.substring(0, tem00.length() - 1));

                    //下一版本start-end-line
                    String nextRange = range.split("-")[1];
                    String tem1 = nextRange.split(",")[0];
                    String tem11 = nextRange.split(",")[1];
                    Integer nextStartLine = Integer.valueOf(tem1.substring(1, tem1.length()));
                    Integer nextEndLine = Integer.valueOf(tem11.substring(0, tem11.length() - 1));

                    if (currStartLine <= bug_lines && currEndLine >= bug_lines) {
                        start_line = currStartLine;
                        end_line = currEndLine;
                        nextstart_line = nextStartLine;
                        nextend_line = nextEndLine;
                        description = item.getString("description");
                    }
                } else {
                    String pretem = range.split(",")[0];
                    Integer currStartLine = Integer.valueOf(pretem.substring(1, pretem.length()));
                    String endtem = range.split(",")[1];
                    Integer currEndLine = Integer.valueOf(endtem.substring(0, endtem.length() - 1));

                    if (currStartLine <= bug_lines && currEndLine >= bug_lines) {
                        start_line = currStartLine;
                        end_line = currEndLine;
                        description = item.getString("description");
                    }
                }
            }
        }
        if (start_line == 0 && end_line == 0) {
            start_line = bug_lines;
            end_line = bug_lines;
            description = "no change";
        }
        JSONObject json = new JSONObject();
        json.put("start_line",start_line);
        json.put("end_line",end_line);
        json.put("nextstart_line",nextstart_line);
        json.put("nextend_line",nextend_line);
        json.put("description",description);
        return json;
    }
}
