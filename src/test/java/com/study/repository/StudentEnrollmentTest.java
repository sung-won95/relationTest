package com.study.repository;

import com.study.entity.Course;
import com.study.entity.Enrollment;
import com.study.entity.Student;
import com.study.repository.CourseRepository;
import com.study.repository.EnrollmentRepository;
import com.study.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentEnrollmentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("Student와 Enrollment의 cascade 동작 테스트 - Student 저장 시 Enrollment도 함께 저장")
    void testCascadePersist() {
        // given
        Student student = new Student("John Doe");
        Course course1 = new Course();
        course1.setTitle("Spring Data JPA");
        Course course2 = new Course();
        course2.setTitle("Spring Boot");

        // course는 cascade가 설정되어 있지 않으므로 먼저 저장
        courseRepository.save(course1);
        courseRepository.save(course2);

        // Enrollment 생성 및 연관관계 설정
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setEnrolledDate(LocalDate.now());
        enrollment1.setCourse(course1);
        student.addEnrollment(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setEnrolledDate(LocalDate.now());
        enrollment2.setCourse(course2);
        student.addEnrollment(enrollment2);

        // when
        // Student만 저장하면 Enrollment도 함께 저장되어야 함 (cascade 설정으로 인해)
        Student savedStudent = studentRepository.save(student);
        
        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // then
        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(foundStudent.getEnrollments()).hasSize(2);
        
        // Enrollment도 독립적으로 존재하는지 확인
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        assertThat(allEnrollments).hasSize(2);
    }

    @Test
    @DisplayName("Student와 Enrollment의 orphanRemoval 동작 테스트 - Enrollment 제거 시 DB에서도 삭제")
    void testOrphanRemoval() {
        // given
        Student student = new Student("Jane Doe");
        Course course = new Course();
        course.setTitle("Hibernate");
        
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrolledDate(LocalDate.now());
        enrollment.setCourse(course);
        student.addEnrollment(enrollment);
        
        Student savedStudent = studentRepository.save(student);
        Long enrollmentId = savedStudent.getEnrollments().get(0).getId();
        
        entityManager.flush();
        entityManager.clear();

        // when
        Student loadedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        loadedStudent.getEnrollments().clear(); // 모든 Enrollment 연관관계 제거
        
        studentRepository.save(loadedStudent);
        entityManager.flush();
        entityManager.clear();

        // then
        // orphanRemoval=true로 인해 연관관계가 끊어진 Enrollment는 DB에서 삭제되어야 함
        Optional<Enrollment> deletedEnrollment = enrollmentRepository.findById(enrollmentId);
        assertThat(deletedEnrollment).isEmpty();
    }

    @Test
    @DisplayName("Student 삭제 시 cascade로 인해 연관된 Enrollment도 함께 삭제")
    void testCascadeRemove() {
        // given
        Student student = new Student("Robert Smith");
        Course course = new Course();
        course.setTitle("JUnit 5");
        
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrolledDate(LocalDate.now());
        enrollment.setCourse(course);
        student.addEnrollment(enrollment);
        
        Student savedStudent = studentRepository.save(student);
        Long enrollmentId = savedStudent.getEnrollments().get(0).getId();
        
        entityManager.flush();
        entityManager.clear();

        // when
        studentRepository.deleteById(savedStudent.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        // Student가 삭제되면 cascade로 인해 연관된 Enrollment도 삭제되어야 함
        Optional<Student> deletedStudent = studentRepository.findById(savedStudent.getId());
        assertThat(deletedStudent).isEmpty();
        
        Optional<Enrollment> deletedEnrollment = enrollmentRepository.findById(enrollmentId);
        assertThat(deletedEnrollment).isEmpty();
    }

    @Test
    @DisplayName("특정 Enrollment만 orphanRemoval로 제거")
    void testRemoveSpecificEnrollment() {
        // given
        Student student = new Student("Alice Johnson");
        
        Course course1 = new Course();
        course1.setTitle("Spring Security");
        Course course2 = new Course();
        course2.setTitle("Spring Cloud");
        
        courseRepository.save(course1);
        courseRepository.save(course2);

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setEnrolledDate(LocalDate.now());
        enrollment1.setCourse(course1);
        student.addEnrollment(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setEnrolledDate(LocalDate.now());
        enrollment2.setCourse(course2);
        student.addEnrollment(enrollment2);
        
        Student savedStudent = studentRepository.save(student);
        Long enrollment1Id = savedStudent.getEnrollments().get(0).getId();
        
        entityManager.flush();
        entityManager.clear();

        // when
        Student loadedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        Enrollment enrollmentToRemove = loadedStudent.getEnrollments().get(0);
        loadedStudent.removeEnrollment(enrollmentToRemove); // 특정 Enrollment만 제거
        
        studentRepository.save(loadedStudent);
        entityManager.flush();
        entityManager.clear();

        // then
        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(updatedStudent.getEnrollments()).hasSize(1);
        
        Optional<Enrollment> deletedEnrollment = enrollmentRepository.findById(enrollment1Id);
        assertThat(deletedEnrollment).isEmpty(); // orphanRemoval로 인해 DB에서도 삭제되어야 함
    }
}
