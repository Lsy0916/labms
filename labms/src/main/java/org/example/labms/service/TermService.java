package org.example.labms.service;

import org.example.labms.dto.TermDTO;
import org.example.labms.model.Term;
import org.example.labms.repository.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TermService {

    @Autowired
    private TermRepository termRepository;

    /**
     * 根据系统当前日期获取当前学期
     * @return 当前学期信息
     */
    public TermDTO getCurrentTerm() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 查询当前学期
        Optional<Term> termOptional = termRepository.findCurrentTerm(currentDate);
        // 如果找到当前学期，则返回DTO
        if (termOptional.isPresent()) {
            Term term = termOptional.get();
            return new TermDTO(
                term.getSemesterId(),
                term.getName(),
                term.getStartDate(),
                term.getEndDate()
            );
        } else {
            // 如果没有找到当前学期，则返回空的DTO
            return new TermDTO();
        }
    }

    /**
     * 根据学期ID获取学期信息
     * @param semesterId 学期ID
     * @return 学期信息
     */
    public TermDTO getTermBySemesterId(String semesterId) {
        Optional<Term> termOptional = termRepository.findBySemesterId(semesterId);
        
        if (termOptional.isPresent()) {
            Term term = termOptional.get();
            return new TermDTO(
                term.getSemesterId(),
                term.getName(),
                term.getStartDate(),
                term.getEndDate()
            );
        } else {
            return new TermDTO();
        }
    }
}