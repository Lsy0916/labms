package org.example.labms.dto;

public class UserInfoResponseDTO {
    private int id;
    private String userId;
    private String name;
    private String gender;
    private String classId;
    private String major;
    private String phone;
    private String email;
    private String roleId;
    private String role;

    // Constructors
    public UserInfoResponseDTO() {}

    public UserInfoResponseDTO(int id, String userId, String name, String gender, String classId, 
                              String major, String phone, String email, String roleId, String role) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.classId = classId;
        this.major = major;
        this.phone = phone;
        this.email = email;
        this.roleId = roleId;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserInfoResponseDTO{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", classId='" + classId + '\'' +
                ", major='" + major + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", roleId='" + roleId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}