package org.example.labms.controller;

import org.example.labms.dto.TermDTO;
import org.example.labms.service.CourseScheduleService;
import org.example.labms.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TermController {

    @Autowired
    private TermService termService;
    
    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * 获取当前学期信息
     * @return 当前学期信息
     */
    @GetMapping("/current_term")
    public ResponseEntity<TermDTO> getCurrentTerm() {
        TermDTO term = termService.getCurrentTerm();
        return ResponseEntity.ok(term);
    }
    
    /**
     * 获取当前学期信息（通过课程服务）
     * @return 当前学期信息
     */
    @GetMapping("/user/current_term")
    public ResponseEntity<TermDTO> getCurrentTermViaCourseService() {
        TermDTO term = courseScheduleService.getCurrentTerm();
        return ResponseEntity.ok(term);
    }
}