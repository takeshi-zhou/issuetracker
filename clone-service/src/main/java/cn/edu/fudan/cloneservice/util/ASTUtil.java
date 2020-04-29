package cn.edu.fudan.cloneservice.util;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.Set;

public class ASTUtil {

    public static String getCode(int startLine, int endLine, String filePath) {
        StringBuilder code = new StringBuilder();
        String s = "";
        int line = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                if (line >= startLine && line <= endLine) {
                    code.append(s);
                    code.append("/n");
                }
                line++;
                if (line > endLine){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    public static int getIncreasedLineNum(String fileName, String line, int startLine){
        String s = "";
        int num = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((s = bufferedReader.readLine()) != null) {
                s = s.trim();
                if(line.equals(s) && num > startLine){
                    return num;
                }
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
