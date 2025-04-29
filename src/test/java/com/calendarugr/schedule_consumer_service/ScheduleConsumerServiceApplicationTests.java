package com.calendarugr.schedule_consumer_service;

import com.calendarugr.schedule_consumer_service.dtos.ExtraClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubscriptionDTO;
import com.calendarugr.schedule_consumer_service.entities.ClassInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScheduleConsumerServiceApplicationTests {

	private static String apiKey;

	public ScheduleConsumerServiceApplicationTests() {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("API_KEY", dotenv.get("API_KEY"));
		apiKey = dotenv.get("API_KEY");
	}

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private final String BASE_URL = "/schedule-consumer";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetClassesFromGroup() throws Exception {

        String grade = "Grado en Ingeniería Informática";
        String subject = "Ingeniería de Servidores";
        String group = "A";
        ClassInfo mockClass = new ClassInfo();
		mockClass.setDay("Viernes");

		System.out.println("API Key: " + apiKey);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/classes-from-group")
                .param("grade", grade)
                .param("subject", subject)
                .param("group", group)
                .header("X-Api-Key", apiKey))
        .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].day").value("Viernes"));

    }

    @Test
    void testValidateExtraClass() throws Exception {
        ExtraClassDTO extraClass = new ExtraClassDTO();
        extraClass.setClassroom("06");
        extraClass.setDay("Viernes");
        extraClass.setDate(LocalDate.of(2024, 11, 22));
        extraClass.setInitHour(LocalTime.of(15, 40));
        extraClass.setFinishHour(LocalTime.of(17, 20));
        extraClass.setGroupName("A");
        extraClass.setSubjectName("Ingeniería de Servidores");
        extraClass.setTeacher("Juanlu");
        extraClass.setGradeName("Grado en Ingeniería Informática");
        extraClass.setFacultyName("E.T.S. de Ingenierías Informática y de Telecomunicación");
        extraClass.setTitle("Corrección práctica 2");
        extraClass.setType("GROUP");
        extraClass.setGroupName("A");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/extraclass-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Api-Key", apiKey)
                        .content(objectMapper.writeValueAsString(extraClass)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("false"));

    }
    
    @Test
    void testGetGrades() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/grades")
                        .header("X-Api-Key", apiKey))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(6)); 
    }

    @Test
    void testGetSubjectsGroups() throws Exception {
        String grade = "Grado en Ingeniería Informática";
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/subjects-groups")
                        .param("grade", grade)
                        .header("X-Api-Key", apiKey))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].subject").value("Cálculo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].groups").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].groups.length()").value(22));
    }

    @Test
    void testValidateSubscription() throws Exception {
        SubscriptionDTO validSubscription = new SubscriptionDTO();
        validSubscription.setGrade("Grado en Ingeniería Informática");
        validSubscription.setSubject("Ingeniería de Servidores");
        validSubscription.setGroup("A");

        SubscriptionDTO invalidSubscription = new SubscriptionDTO();
        invalidSubscription.setGrade("Grado en Ingeniería Informática");
        invalidSubscription.setSubject("Ingeniería de Servidores");
        invalidSubscription.setGroup("Z");

        // Test valid subscription
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/subscription-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Api-Key", apiKey)
                        .content(objectMapper.writeValueAsString(validSubscription)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("true"));

        // Test invalid subscription
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/subscription-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Api-Key", apiKey)
                        .content(objectMapper.writeValueAsString(invalidSubscription)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("false"));

    }

    @Test
    void testGetClassesFromGroup_unauthorized() throws Exception {
        String grade = "Grado en Ingeniería Informática";
        String subject = "Ingeniería de Servidores";
        String group = "A";

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/classes-from-group")
                        .param("grade", grade)
                        .param("subject", subject)
                        .param("group", group)
                        .header("X-Api-Key", "wrong-api-key"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}