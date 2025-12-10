package org.example.labms.dto.request;

public class UpdateUserInfoRequest {
    private String userId;
    private String phone;
    private String email;
    private String roleId;

    public UpdateUserInfoRequest() {
    }

    public UpdateUserInfoRequest(String userId, String phone, String email, String roleId) {
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UpdateUserInfoRequest{" +
                "userId='" + userId + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}