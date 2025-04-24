package com.study.repository;

import com.study.entity.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // N+1 문제를 발생시키는 기본 메서드는 JpaRepository에 내장된 findById를 사용
    
    // 해결 방법 1: Fetch Join 사용
    @Query("SELECT s FROM Student s JOIN FETCH s.enrollments e JOIN FETCH e.course WHERE s.id = :id")
    Student findStudentWithCoursesByFetchJoin(@Param("id") Long id);
    
    // 해결 방법 2: Entity Graph 사용
    @EntityGraph(value = "Student.withEnrollmentsAndCourses", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT s FROM Student s WHERE s.id = :id")
    Student findStudentWithCoursesByEntityGraph(@Param("id") Long id);

}
