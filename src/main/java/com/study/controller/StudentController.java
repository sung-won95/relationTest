package com.study.controller;

import com.study.dto.StudentCreateRequest;
import com.study.entity.Enrollment;
import com.study.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    /**
     * 학생 생성 및 수강 정보 등록 API (cascade 테스트)
     */
    @PostMapping
    public ResponseEntity<Long> createStudent(@RequestBody StudentCreateRequest request) {
        Long studentId = studentService.createStudentWithCourses(request.getName(), request.getCourses());
        return ResponseEntity.status(HttpStatus.CREATED).body(studentId);
    }
    
    /**
     * 학생의 수강 정보 삭제 API (orphanRemoval 테스트)
     */
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> removeEnrollment(
            @PathVariable Long studentId,
            @PathVariable Long courseId
    ) {
        studentService.removeEnrollment(studentId, courseId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 학생 삭제 API (cascade 삭제 테스트)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 학생의 수강 정보 조회 API
     */
    @GetMapping("/{id}/enrollments")
    public ResponseEntity<Integer> getStudentEnrollments(@PathVariable Long id) {
        List<Enrollment> enrollments = studentService.getStudentEnrollments(id);
        return ResponseEntity.ok(enrollments.size());
    }
}
