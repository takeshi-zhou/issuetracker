package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Package {

    private String uuid;
    private String name;
    private int classes;
    private int functions;
    private int ncss;
    private double ccn;
    private int javaDocs;
    private int javaDocsLines;
    private int singleCommentLines;
    private int multiCommentLines;
    private String commit_id;
    private String commit_time;
    private String repo_id;

    public Package(){

    }

    public Package(String uuid, String name, int classes, int functions, int ncss, int javaDocs, int javaDocsLines, int singleCommentLines, int multiCommentLines) {
        this.uuid = uuid;
        this.name = name;
        this.classes = classes;
        this.functions = functions;
        this.ncss = ncss;
        this.javaDocs = javaDocs;
        this.javaDocsLines = javaDocsLines;
        this.singleCommentLines = singleCommentLines;
        this.multiCommentLines = multiCommentLines;
    }
}
