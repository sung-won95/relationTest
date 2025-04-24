package com.study.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("N+1 문제 발생 테스트")
    void testNPlus1Problem() {
        // given
        Long studentId = 1L;
        
        // when & then
        System.out.println("\n========== N+1 문제 발생 테스트 시작 ==========");
        studentService.printStudentCoursesWithNPlus1Problem(studentId);
        System.out.println("========== N+1 문제 발생 테스트 끝 ==========\n");
    }

    @Test
    @DisplayName("Fetch Join으로 N+1 문제 해결 테스트")
    void testSolveNPlus1WithFetchJoin() {
        // given
        Long studentId = 1L;
        
        // when & then
        System.out.println("\n========== Fetch Join 해결 테스트 시작 ==========");
        studentService.printStudentCoursesWithFetchJoin(studentId);
        System.out.println("========== Fetch Join 해결 테스트 끝 ==========\n");
    }

    @Test
    @DisplayName("Entity Graph로 N+1 문제 해결 테스트")
    void testSolveNPlus1WithEntityGraph() {
        // given
        Long studentId = 1L;
        
        // when & then
        System.out.println("\n========== Entity Graph 해결 테스트 시작 ==========");
        studentService.printStudentCoursesWithEntityGraph(studentId);
        System.out.println("========== Entity Graph 해결 테스트 끝 ==========\n");
    }
}
