package org.example.labms.controller;

import org.example.labms.dto.CourseScheduleDTO;
import org.example.labms.dto.TermDTO;
import org.example.labms.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * 获取当前学期用户课程信息
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 课程信息列表
     */
    @GetMapping("/user/courses")
    public ResponseEntity<List<CourseScheduleDTO>> getUserCourses(
            @RequestParam String roleId,
            @RequestParam String userId) {
        
        List<CourseScheduleDTO> courses = courseScheduleService.getUserCourses(roleId, userId);
        
        return ResponseEntity.ok(courses);
    }

}