package org.example.labms.controller;

import org.example.labms.model.ComputerLab;
import org.example.labms.service.ComputerLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/computer-labs")
@CrossOrigin
public class ComputerLabController {

    @Autowired
    private ComputerLabService computerLabService;

    /**
     * 获取所有机房信息
     * @return 机房信息列表
     */
    @GetMapping
    public List<ComputerLab> getAllComputerLabs() {
        return computerLabService.getAllComputerLabs();
    }

    /**
     * 根据机房ID获取机房信息
     * @param roomId 机房ID
     * @return 机房信息
     */
    @GetMapping("/{roomId}")
    public ComputerLab getComputerLabByRoomId(@PathVariable String roomId) {
        return computerLabService.getComputerLabByRoomId(roomId);
    }

    /**
     * 创建机房
     * @param computerLab 机房对象
     * @return 创建后的机房对象
     */
    @PostMapping
    public ComputerLab createComputerLab(@RequestBody ComputerLab computerLab) {
        return computerLabService.createComputerLab(computerLab);
    }
    
    /**
     * 更新机房信息
     * @param roomId 机房ID
     * @param name 机房名称
     * @param totalSeats 总座位数
     * @param allowedRoles 允许使用的角色
     * @param status 状态
     * @param managerId 管理员ID
     * @param equipmentInfo 设备信息
     * @return 更新后的机房对象
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<ComputerLab> updateComputerLabInfo(
            @PathVariable String roomId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer totalSeats,
            @RequestParam(required = false) String allowedRoles,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String equipmentInfo) {
        
        ComputerLab updatedLab = computerLabService.updateComputerLabInfo(
                roomId,
                name,
                totalSeats,
                allowedRoles,
                status,
                managerId,
                equipmentInfo
        );
        
        if (updatedLab != null) {
            return ResponseEntity.ok(updatedLab);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除机房
     * @param roomId 机房ID
     * @return 删除结果
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteComputerLab(@PathVariable String roomId) {
        try {
            computerLabService.deleteComputerLab(roomId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}