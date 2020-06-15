package cn.edu.fudan.cloneservice.util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ASTUtil {

    public CodeLocation getCode(int startLine, int endLine, int cloneStartLine, int cloneEndLine, String filePath) {
        StringBuilder code = new StringBuilder();
        String s;
        int line = 1;
        List<String> num = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                if (line >= startLine && line <= endLine) {

                    code.append(s);
                    code.append("/n");
                    if(line >= cloneStartLine && line <= cloneEndLine){
                        //判断是否是空行或者注释
                        if(!s.isEmpty() && !s.trim().startsWith("//")
                                && !s.trim().startsWith("/**")
                                && !s.trim().startsWith("*")){
                            num.add(line+"");
                        }
                    }
                }
                line++;
                if (line > endLine){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CodeLocation(num, code.toString());
    }

    public class CodeLocation{
        private List<String> num;
        private String code;

        public CodeLocation(List<String> num, String code) {
            this.num = num;
            this.code = code;
        }

        public List<String> getNum() {
            return num;
        }

        public String getCode() {
            return code;
        }
    }

}
