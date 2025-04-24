package com.study.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Enrollment {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate enrolledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    public static Enrollment createEnrollment(Student student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrolledDate(LocalDate.now());
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        
        student.getEnrollments().add(enrollment);
        course.getEnrollments().add(enrollment);
        
        return enrollment;
    }
}
