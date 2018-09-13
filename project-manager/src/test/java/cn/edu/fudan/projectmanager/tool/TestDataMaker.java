package cn.edu.fudan.projectmanager.tool;

import cn.edu.fudan.projectmanager.domain.Project;

public class TestDataMaker {


    public  Project projectMakerPro1(){
        Project project = new Project();
        project.setUuid("pro1");
        project.setName("Java");
        project.setLanguage("Java");
        project.setUrl("https://github.com/DuGuQiuBai/Java");
        project.setVcs_type("git");
        project.setAccount_id("1");
        project.setDownload_status("Downloaded");
        project.setScan_status("Not Scanned");
        project.setDescription("27天成为Java大神");
        project.setRepo_id("227a91de-a522-11e8-8fa0-d067e5ea858d");
        return project;
    }

    public  Project projectMakerPro2(){
        Project project = new Project();
        project.setUuid("pro2");
        project.setName("Java");
        project.setLanguage("Java");
        project.setUrl("https://github.com/spotify/docker-maven-plugin");
        project.setVcs_type("git");
        project.setAccount_id("1");
        project.setDownload_status("Downloaded");
        project.setScan_status("Not Scanned");
        project.setDescription("27天成为Java大神");
        project.setRepo_id("RepoId");
        return project;
    }

    public  Project projectMakerPro1Update(){
        Project project = new Project();
        project.setUuid("pro1");
        project.setName("UpdateData");
        project.setLanguage("Java");
        project.setUrl("https://github.com/spotify/docker-maven-plugin");
        project.setVcs_type("git");
        project.setAccount_id("1");
        project.setDownload_status("Downloaded");
        project.setScan_status("Not Scanned");
        project.setDescription("27天成为Java大神");
        project.setRepo_id("RepoId");
        return project;
    }
}
