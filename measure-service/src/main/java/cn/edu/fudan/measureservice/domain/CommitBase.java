package cn.edu.fudan.measureservice.domain;

import java.util.ArrayList;
import java.util.List;

public class CommitBase {
    private List<Developer> authors;
    private int addLines;
    private int delLines;


    public List<Developer> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Developer> authors) {
        this.authors = authors;
    }

    public void addAuthor(Developer developer){
        if(authors == null){
            authors = new ArrayList<>();
        }
        authors.add(developer);
    }

    public int getAddLines() {
        return addLines;
    }

    public void setAddLines(int addLines) {
        this.addLines = addLines;
    }

    public int getDelLines() {
        return delLines;
    }

    public void setDelLines(int delLines) {
        this.delLines = delLines;
    }
}
