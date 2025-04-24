package com.study.controller;

import com.study.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}/n-plus-1")
    public String getStudentCoursesWithNPlus1Problem(@PathVariable Long id) {
        studentService.printStudentCoursesWithNPlus1Problem(id);
        return "콘솔 로그를 확인하세요 - N+1 문제 발생";
    }

    @GetMapping("/{id}/fetch-join")
    public String getStudentCoursesWithFetchJoin(@PathVariable Long id) {
        studentService.printStudentCoursesWithFetchJoin(id);
        return "콘솔 로그를 확인하세요 - Fetch Join으로 해결";
    }

    @GetMapping("/{id}/entity-graph")
    public String getStudentCoursesWithEntityGraph(@PathVariable Long id) {
        studentService.printStudentCoursesWithEntityGraph(id);
        return "콘솔 로그를 확인하세요 - Entity Graph로 해결";
    }
}
