package cn.edu.fudan.cloneservice.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FiletravesNIOimpl implements Filetraves{


    String dir;



    public FiletravesNIOimpl(String dir){
        this.dir = dir;
    }



    @Override
    public List<File> directoryAllFileList() {
        Path path = Paths.get(dir);
        FilterFileVisitor filterFileVisitor = new FilterFileVisitor();
        try {
            Files.walkFileTree(path,filterFileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filterFileVisitor.getFileLists();
    }

    class FilterFileVisitor extends SimpleFileVisitor<Path>{

        private List<File> fileLists = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

            if (file.toString().endsWith(".java")){             ///这个地方必须得加.toString
                fileLists.add(file.toFile());
            }
            return super.visitFile(file, attrs);
        }

        public List<File> getFileLists() {
            return fileLists;
        }
    }
}
