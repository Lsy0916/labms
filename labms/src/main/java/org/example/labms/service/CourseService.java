package org.example.labms.service;

import org.example.labms.model.Course;
import org.example.labms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 根据课程ID获取课程名称
     * @param courseId 课程ID
     * @return 课程名称
     */
    public String getCourseNameByCourseId(String courseId) {
        Optional<Course> courseOptional = courseRepository.findByCourseId(courseId);
        if (courseOptional.isPresent()) {
            return courseOptional.get().getName();
        } else {
            // 如果数据库中没有找到对应的课程，则返回默认名称
            return "课程-" + courseId;
        }
    }
    
    /**
     * 根据课程ID获取完整课程信息
     * @param courseId 课程ID
     * @return 课程对象
     */
    public Optional<Course> getCourseByCourseId(String courseId) {
        return courseRepository.findByCourseId(courseId);
    }
}