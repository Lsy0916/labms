package org.example.labms.dto.request;

public class LoginResponse {
    private String token;
    private Object userInfo;

    public LoginResponse() {
    }

    public LoginResponse(String token, Object userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Object userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}