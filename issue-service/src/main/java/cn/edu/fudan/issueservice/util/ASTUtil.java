package cn.edu.fudan.issueservice.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ASTUtil {

    public static String getCode(int startLine, int endLine, String filePath) throws Exception{
        StringBuilder code = new StringBuilder();
        String s = "";
        int line = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                if (line >= startLine && line <= endLine) {
                    code.append(s);
                    code.append("\n");
                }
                line++;
                if (line > endLine) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return code.toString();
    }
}
