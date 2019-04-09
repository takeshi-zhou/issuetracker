package cn.edu.fudan.cloneservice.domain;

public class PackageInfo {


    private String uuid;
    private String repo_id;
    private String commit_id;
    private String package_name;
    private int method_num;
    private int clone_ins_num;

    public PackageInfo(String uuid, String repo_id, String commit_id, String package_name, int method_num, int clone_ins_num) {
        this.uuid = uuid;
        this.repo_id = repo_id;
        this.commit_id = commit_id;
        this.package_name = package_name;
        this.method_num = method_num;
        this.clone_ins_num = clone_ins_num;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public int getMethod_num() {
        return method_num;
    }

    public void setMethod_num(int method_num) {
        this.method_num = method_num;
    }

    public int getClone_ins_num() {
        return clone_ins_num;
    }

    public void setClone_ins_num(int clone_ins_num) {
        this.clone_ins_num = clone_ins_num;
    }
}
