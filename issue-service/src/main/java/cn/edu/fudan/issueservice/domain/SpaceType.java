package cn.edu.fudan.issueservice.domain;

public enum SpaceType {
    DEVELOPER("developer"),
    PROJECT("project"),
    FILE("file"),
    PACKAGE("package");

    private final String level;

    private SpaceType(String level) {
        this.level = level;
    }

    public String getLevel() {
        return this.level;
    }
}
