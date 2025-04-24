## 1. 도메인 구조

- **학생(Student)**: 여러 코스를 수강할 수 있습니다.
- **코스(Course)**: 여러 학생이 수강할 수 있습니다.
- **수강정보(Enrollment)**: 학생과 코스의 다대다 관계를 관리합니다.

## 2. 연관관계 설계

- Student와 Enrollment: OneToMany(1:N)
- Course와 Enrollment: OneToMany(1:N)
- Enrollment와 Student: ManyToOne(N:1)
- Enrollment와 Course: ManyToOne(N:1)

## 3. N+1 문제 

### N+1 문제란?

N+1 문제는 ORM(Object-Relational Mapping)에서 흔히 발생하는 성능 문제로, 처음에 엔티티를 조회하는 1개의 쿼리와 그 엔티티와 연관된 컬렉션을 가져오기 위한 N개의 추가 쿼리가 발생하는 현상입니다.

### 문제 발생 코드
```java
Student student = studentRepository.findById(studentId).orElseThrow();
for (Enrollment enrollment : student.getEnrollments()) {
    System.out.println(enrollment.getCourse().getTitle());
}
```

## 4. 해결 방법

### 4.1 Fetch Join 사용

```java
@Query("SELECT s FROM Student s JOIN FETCH s.enrollments e JOIN FETCH e.course WHERE s.id = :id")
Student findStudentWithCoursesByFetchJoin(@Param("id") Long id);
```

### 4.2 Entity Graph 사용

```java
@NamedEntityGraph(name = "Student.withEnrollmentsAndCourses",
    attributeNodes = @NamedAttributeNode(value = "enrollments", subgraph = "enrollmentsWithCourse"),
    subgraphs = @NamedSubgraph(name = "enrollmentsWithCourse", 
                               attributeNodes = @NamedAttributeNode("course")))
```

```java
@EntityGraph(value = "Student.withEnrollmentsAndCourses")
@Query("SELECT s FROM Student s WHERE s.id = :id")
Student findStudentWithCoursesByEntityGraph(@Param("id") Long id);
```

## 5. 테스트 방법

### 5.1 애플리케이션 실행

1. 애플리케이션을 실행합니다.
2. 다음 URL을 통해 각각의 방식으로 테스트할 수 있습니다:
   - N+1 문제 발생: http://localhost:8080/api/students/1/courses/n-plus-1
   - Fetch Join 해결: http://localhost:8080/api/students/1/courses/fetch-join
   - Entity Graph 해결: http://localhost:8080/api/students/1/courses/entity-graph

### 5.2 테스트 클래스 실행

`StudentServiceTest` 클래스를 실행하여 각 방식의 결과를 콘솔에서 확인할 수 있습니다.

