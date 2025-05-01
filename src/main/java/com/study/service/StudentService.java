package com.study.service;

import com.study.entity.Course;
import com.study.entity.Enrollment;
import com.study.entity.Student;
import com.study.repository.CourseRepository;
import com.study.repository.EnrollmentRepository;
import com.study.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

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
    
    /**
     * 학생 생성 및 수강 정보 등록 (Cascade.PERSIST 테스트)
     */
    @Transactional
    public Long createStudentWithCourses(String studentName, List<String> courseTitles) {
        // 학생 생성
        Student student = new Student(studentName);
        
        // 코스 생성 및 수강 정보 등록
        for (String title : courseTitles) {
            Course course = new Course();
            course.setTitle(title);
            courseRepository.save(course);
            
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledDate(LocalDate.now());
            enrollment.setCourse(course);
            student.addEnrollment(enrollment);
        }
        
        // 학생 저장 - cascade로 인해 enrollment도 함께 저장됨
        Student savedStudent = studentRepository.save(student);
        return savedStudent.getId();
    }
    
    /**
     * 학생의 특정 수강 정보 삭제 (OrphanRemoval 테스트)
     */
    @Transactional
    public void removeEnrollment(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        
        // 제거할 수강 정보 찾기
        Enrollment enrollmentToRemove = student.getEnrollments().stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow();
        
        // 수강 정보 제거 - orphanRemoval로 인해 DB에서도 삭제됨
        student.removeEnrollment(enrollmentToRemove);
        
        studentRepository.save(student);
    }
    
    /**
     * 학생 삭제 (Cascade.REMOVE 테스트)
     */
    @Transactional
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }
    
    /**
     * 학생의 모든 수강 정보 조회
     */
    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        return student.getEnrollments();
    }
}
