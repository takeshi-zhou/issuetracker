package cn.edu.fudan.cloneservice.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;

/**
 * Created by njzhan
 * <p>
 * Date :2019-09-02
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public class LineNumUtil {
    public long getRepoLineNumber(String repo_url){
        Filetraves filetraves = new FiletravesNIOimpl(repo_url);
        List<File> fileList = filetraves.directoryAllFileList();
        long sum = 0;
        for(File f: fileList){
            long num = getLineNumber(f);
            sum += num;
        }
        return sum;
    }
    public long getLineNumber(File file) {
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
                lineNumberReader.skip(Long.MAX_VALUE);
                long lines = lineNumberReader.getLineNumber() + 1;
                fileReader.close();
                lineNumberReader.close();
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
