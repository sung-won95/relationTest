package com.study.config;

import com.study.entity.Course;
import com.study.entity.Enrollment;
import com.study.entity.Student;
import com.study.repository.CourseRepository;
import com.study.repository.EnrollmentRepository;
import com.study.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostConstruct
    public void init() {
        initData();
    }

    @Transactional
    public void initData() {
        // 학생 생성
        Student student1 = new Student();
        student1.setName("홍길동");
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("김철수");
        studentRepository.save(student2);

        // 코스 생성
        Course course1 = new Course();
        course1.setTitle("스프링 부트");
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setTitle("JPA 프로그래밍");
        courseRepository.save(course2);

        Course course3 = new Course();
        course3.setTitle("리액트 기초");
        courseRepository.save(course3);

        // 수강 정보 생성
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setStudent(student1);
        enrollment1.setCourse(course1);
        enrollment1.setEnrolledDate(LocalDate.now().minusDays(10));
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setStudent(student1);
        enrollment2.setCourse(course2);
        enrollment2.setEnrolledDate(LocalDate.now().minusDays(5));
        enrollmentRepository.save(enrollment2);

        Enrollment enrollment3 = new Enrollment();
        enrollment3.setStudent(student2);
        enrollment3.setCourse(course2);
        enrollment3.setEnrolledDate(LocalDate.now().minusDays(3));
        enrollmentRepository.save(enrollment3);

        Enrollment enrollment4 = new Enrollment();
        enrollment4.setStudent(student2);
        enrollment4.setCourse(course3);
        enrollment4.setEnrolledDate(LocalDate.now().minusDays(1));
        enrollmentRepository.save(enrollment4);
    }
}
