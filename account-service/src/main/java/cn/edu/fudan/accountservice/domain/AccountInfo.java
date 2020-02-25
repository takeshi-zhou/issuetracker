package cn.edu.fudan.accountservice.domain;

/**
 * @author WZY
 * @version 1.0
 **/
public class AccountInfo {

    private String username;

    private String token;

    private int right;

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public AccountInfo() {
    }

    public AccountInfo(String username, String token, int right) {
        this.username = username;
        this.token = token;
        this.right = right;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
