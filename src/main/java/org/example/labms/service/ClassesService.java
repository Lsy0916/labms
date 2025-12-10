package org.example.labms.service;

import org.example.labms.model.Classes;
import org.example.labms.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClassesService {
    
    @Autowired
    private ClassesRepository classesRepository;
    
    // 根据ID获取教室
    public Optional<Classes> getClassById(int id) {
        return classesRepository.findById(id);
    }
    
    // 根据classId获取教室
    public Optional<Classes> getClassByClassId(String classId) {
        return classesRepository.findByClassId(classId);
    }
    
    // 根据名称获取教室
    public Optional<Classes> getClassByName(String name) {
        return classesRepository.findByName(name);
    }
    
    // 创建或更新教室
    public Classes saveClass(Classes classObj) {
        return classesRepository.save(classObj);
    }
}