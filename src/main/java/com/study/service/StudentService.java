package com.study.service;

import com.study.entity.Enrollment;
import com.study.entity.Student;
import com.study.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * N+1 문제가 발생하는 예제 메서드
     * 1. 처음에 학생 조회 쿼리 1번 실행
     * 2. 각 수강 정보마다 코스 조회 쿼리 N번 실행
     */
    @Transactional(readOnly = true)
    public void printStudentCoursesWithNPlus1Problem(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        System.out.println("학생 이름: " + student.getName());
        
        System.out.println("=== N+1 문제 발생 시작 ===");
        for (Enrollment enrollment : student.getEnrollments()) {
            // 지연 로딩으로 인해 루프마다 쿼리가 발생
            System.out.println("수강한 코스 제목: " + enrollment.getCourse().getTitle());
            System.out.println("수강 날짜: " + enrollment.getEnrolledDate());
        }
        System.out.println("=== N+1 문제 발생 종료 ===");
    }

    /**
     * Fetch Join으로 N+1 문제 해결
     */
    @Transactional(readOnly = true)
    public void printStudentCoursesWithFetchJoin(Long studentId) {
        Student student = studentRepository.findStudentWithCoursesByFetchJoin(studentId);
        System.out.println("학생 이름: " + student.getName());
        
        System.out.println("=== Fetch Join 사용 시작 ===");
        for (Enrollment enrollment : student.getEnrollments()) {
            // Fetch Join으로 인해 추가 쿼리 발생하지 않음
            System.out.println("수강한 코스 제목: " + enrollment.getCourse().getTitle());
            System.out.println("수강 날짜: " + enrollment.getEnrolledDate());
        }
        System.out.println("=== Fetch Join 사용 종료 ===");
    }

    /**
     * Entity Graph로 N+1 문제 해결
     */
    @Transactional(readOnly = true)
    public void printStudentCoursesWithEntityGraph(Long studentId) {
        // SQL 로그 확인을 위한 구분선
        System.out.println("=============== Entity Graph 쿼리 시작 ===============");
        Student student = studentRepository.findStudentWithCoursesByEntityGraph(studentId);
        System.out.println("=============== Entity Graph 쿼리 종료 ===============");
        
        System.out.println("학생 이름: " + student.getName());
        
        System.out.println("=== Entity Graph 사용 시작 ===");
        for (Enrollment enrollment : student.getEnrollments()) {
            // Entity Graph로 인해 추가 쿼리 발생하지 않음
            System.out.println("수강한 코스 제목: " + enrollment.getCourse().getTitle());
            System.out.println("수강 날짜: " + enrollment.getEnrolledDate());
        }
        System.out.println("=== Entity Graph 사용 종료 ===");
    }
}
