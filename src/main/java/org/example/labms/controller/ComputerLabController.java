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

    @GetMapping
    public List<ComputerLab> getAllComputerLabs() {
        return computerLabService.getAllComputerLabs();
    }

    @GetMapping("/{roomId}")
    public ComputerLab getComputerLabByRoomId(@PathVariable String roomId) {
        return computerLabService.getComputerLabByRoomId(roomId);
    }

    @PostMapping
    public ComputerLab createComputerLab(@RequestBody ComputerLab computerLab) {
        return computerLabService.createComputerLab(computerLab);
    }
    
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