package com.study.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NamedEntityGraph(
    name = "Student.withEnrollmentsAndCourses",
    attributeNodes = {
        @NamedAttributeNode(value = "enrollments", subgraph = "enrollments")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "enrollments",
            attributeNodes = {
                @NamedAttributeNode("course")
            }
        )
    }
)
public class Student {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }
}
