package org.example.labms.model;

import jakarta.persistence.*;

@Table(name = "classes")
@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "name")
    private String name;

    @Column(name = "college")
    private String college;

    @Column(name = "student_count")
    private int studentCount;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructors
    public Classes() {
    }

    public Classes(String classId, String name, String college, int studentCount, String createdAt, String updatedAt) {
        this.classId = classId;
        this.name = name;
        this.college = college;
        this.studentCount = studentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
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

    @Override
    public String toString() {
        return "Classes{" +
                "id=" + id +
                ", classId='" + classId + '\'' +
                ", name='" + name + '\'' +
                ", college='" + college + '\'' +
                ", studentCount=" + studentCount +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}