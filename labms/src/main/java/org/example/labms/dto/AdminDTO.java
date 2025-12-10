package org.example.labms.dto;

public class AdminDTO {
    private int id;
    private String adminId;
    private String name;
    private String gender;
    private String department;
    private String position;
    private String phone;
    private String email;
    private String hireDate;
    private String status;
    private String password;
    private String createdAt;
    private String updatedAt;
    private String roleId;
    private String role;
    private String avatarUrl;

    // Constructors
    public AdminDTO() {}

    public AdminDTO(int id, String adminId, String name, String gender, String department, String position,
                    String phone, String email, String hireDate, String status, String password,
                    String createdAt, String updatedAt, String roleId, String role, String avatarUrl) {
        this.id = id;
        this.adminId = adminId;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.hireDate = hireDate;
        this.status = status;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roleId = roleId;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "id=" + id +
                ", adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", hireDate='" + hireDate + '\'' +
                ", status='" + status + '\'' +
                ", password='" + password + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", roleId='" + roleId + '\'' +
                ", role='" + role + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}