package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.StudentCreateRequest;
import com.study.entity.Enrollment;
import com.study.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class) // StudentController만 테스트
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // Service 계층은 모킹(가짜 객체)
    private StudentService studentService;

    @Test
    @DisplayName("N+1 문제 발생 API 테스트")
    void getStudentCoursesWithNPlus1Problem() throws Exception {
        // given
        Long studentId = 1L;
        doNothing().when(studentService).printStudentCoursesWithNPlus1Problem(studentId);

        // when & then
        mockMvc.perform(get("/api/students/{id}/n-plus-1", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string("콘솔 로그를 확인하세요 - N+1 문제 발생"));

        // verify
        verify(studentService).printStudentCoursesWithNPlus1Problem(studentId);
    }

    @Test
    @DisplayName("Fetch Join으로 N+1 문제 해결 API 테스트")
    void getStudentCoursesWithFetchJoin() throws Exception {
        // given
        Long studentId = 1L;
        doNothing().when(studentService).printStudentCoursesWithFetchJoin(studentId);

        // when & then
        mockMvc.perform(get("/api/students/{id}/fetch-join", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string("콘솔 로그를 확인하세요 - Fetch Join으로 해결"));

        // verify
        verify(studentService).printStudentCoursesWithFetchJoin(studentId);
    }

    @Test
    @DisplayName("Entity Graph로 N+1 문제 해결 API 테스트")
    void getStudentCoursesWithEntityGraph() throws Exception {
        // given
        Long studentId = 1L;
        doNothing().when(studentService).printStudentCoursesWithEntityGraph(studentId);

        // when & then
        mockMvc.perform(get("/api/students/{id}/entity-graph", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string("콘솔 로그를 확인하세요 - Entity Graph로 해결"));

        // verify
        verify(studentService).printStudentCoursesWithEntityGraph(studentId);
    }
    
    @Test
    @DisplayName("학생 생성 API 테스트 - cascade persist")
    void createStudent() throws Exception {
        // given
        Long studentId = 1L;
        String studentName = "홍길동";
        List<String> courses = Arrays.asList("수학", "과학");
        
        StudentCreateRequest request = new StudentCreateRequest(studentName, courses);
        
        when(studentService.createStudentWithCourses(eq(studentName), anyList()))
                .thenReturn(studentId);

        // when & then
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(studentId.toString()));

        // verify
        verify(studentService).createStudentWithCourses(eq(studentName), anyList());
    }
    
    @Test
    @DisplayName("수강 정보 삭제 API 테스트 - orphanRemoval")
    void removeEnrollment() throws Exception {
        // given
        Long studentId = 1L;
        Long courseId = 2L;
        
        doNothing().when(studentService).removeEnrollment(studentId, courseId);

        // when & then
        mockMvc.perform(delete("/api/students/{studentId}/courses/{courseId}", studentId, courseId))
                .andExpect(status().isOk());

        // verify
        verify(studentService).removeEnrollment(studentId, courseId);
    }
    
    @Test
    @DisplayName("학생 삭제 API 테스트 - cascade remove")
    void deleteStudent() throws Exception {
        // given
        Long studentId = 1L;
        
        doNothing().when(studentService).deleteStudent(studentId);

        // when & then
        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isOk());

        // verify
        verify(studentService).deleteStudent(studentId);
    }
    
    @Test
    @DisplayName("수강 정보 조회 API 테스트")
    void getStudentEnrollments() throws Exception {
        // given
        Long studentId = 1L;
        List<Enrollment> enrollments = Collections.emptyList();
        
        when(studentService.getStudentEnrollments(studentId)).thenReturn(enrollments);

        // when & then
        mockMvc.perform(get("/api/students/{id}/enrollments", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));  // 빈 리스트이므로 사이즈는 0

        // verify
        verify(studentService).getStudentEnrollments(studentId);
    }
}
