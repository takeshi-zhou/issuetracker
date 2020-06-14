package cn.edu.fudan.cloneservice.util;


import java.io.*;

public class ASTUtil {

    public CodeLocation getCode(int startLine, int endLine, int cloneStartLine, int cloneEndLine, String filePath) {
        StringBuilder code = new StringBuilder();
        String s;
        int line = 1;
        int num = 0;
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
                            num++;
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
        private int num;
        private String code;

        private CodeLocation(int num, String code){
            this.code = code;
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public String getCode() {
            return code;
        }
    }

}
