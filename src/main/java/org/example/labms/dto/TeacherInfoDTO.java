package org.example.labms.dto;

public class TeacherInfoDTO {
    private String userId;
    private String name;
    private String avatarUrl;
    private String roleId;

    public TeacherInfoDTO() {
    }

    public TeacherInfoDTO(String userId, String name, String avatarUrl, String roleId) {
        this.userId = userId;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.roleId = roleId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "TeacherInfoDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}