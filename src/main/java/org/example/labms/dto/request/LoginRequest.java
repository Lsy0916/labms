package org.example.labms.dto.request;

public class LoginRequest {
    private String userId;
    private String password;
    private String roleId;

    public LoginRequest() {
    }

    public LoginRequest(String userId, String password, String roleId) {
        this.userId = userId;
        this.password = password;
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}