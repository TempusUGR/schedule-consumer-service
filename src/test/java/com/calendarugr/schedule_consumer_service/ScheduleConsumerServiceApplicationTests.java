package com.calendarugr.schedule_consumer_service;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import com.calendarugr.schedule_consumer_service.dtos.ClassDTO;
import com.calendarugr.schedule_consumer_service.dtos.SubscriptionDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ScheduleConsumerServiceApplicationTests {

    private final String BASE_URL = "http://schedule-consumer-service/schedule-consumer";
    
    @Autowired
    private WebClient.Builder webClientBuilder;

	@Value("${api.key}") 
    private String apiKey;

	// We check that this request -> localhost:8090/schedule-consumer/classes-from-group?grade=Grado en Ingeniería Informática&subject=Ingeniería de Servidores&group=A
	// Gives us reply us with only one class. Then we check that the class is on "Viernes".
	@Test
	void testGetClassesFromGroup() {
		String grade = "Grado en Ingeniería Informática";
		String subject = "Ingeniería de Servidores";
		String group = "A";

		try {
			List<ClassDTO> classes = webClientBuilder.build()
					.get()
					.uri(BASE_URL + "/classes-from-group?grade=" + grade + "&subject=" + subject + "&group=" + group)
					.header("X-Api-Key", apiKey) // Añadir la cabecera de la API Key
					.retrieve()
					.bodyToFlux(ClassDTO.class)
					.collectList()
					.block();

			// Check that we have only one class
			assert classes != null;
			assert classes.size() == 1;
			// Check that the class is on "Viernes"
			assert classes.get(0).getDay().equals("Viernes");

		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode());
			System.err.println("Response Body: " + e.getResponseBodyAsString());
			throw e; // Re-lanzar la excepción para que el test falle
		}
	}

	// We check that there is 6 objects in the list that we request -> localhost:8090/schedule-consumer/grades
	@Test
	void testGetGrades() { // 6 different knowledge areas
		try {
			List<?> grades = webClientBuilder.build()
					.get()
					.uri(BASE_URL + "/grades")
					.header("X-Api-Key", apiKey) // Añadir la cabecera de la API Key
					.retrieve()
					.bodyToFlux(Object.class)
					.collectList()
					.block();

			assert grades != null;
			assert grades.size() == 6;

		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode());
			System.err.println("Response Body: " + e.getResponseBodyAsString());
			throw e; // Re-lanzar la excepción para que el test falle
		}
	}

	// Test if calling -> localhost:8090/schedule-consumer/subjects-groups?grade=Grado en Ingeniería Informática
	// we get a subject called "Transmisión de Datos y Redes de Computadores (Especialidadtecnologías de la Información)" with 4 groups
	@Test
	void testGetSubjectsGroups() {
		String grade = "Grado en Ingeniería Informática";

		try {
			List<HashMap<String, Object>> subjects = webClientBuilder.build()
					.get()
					.uri(BASE_URL + "/subjects-groups?grade=" + grade)
					.header("X-Api-Key", apiKey) // Añadir la cabecera de la API Key
					.retrieve()
					.bodyToFlux(new ParameterizedTypeReference<HashMap<String, Object>>() {})
					.collectList()
					.block();

			// Iterate over HashMap list to find the key (subject) with the value "Transmisión de Datos y Redes de Computadores (Especialidadtecnologías de la Información)"
			boolean found = false;

			for (HashMap<String, Object> subject : subjects) {
				if (subject.get("subject").equals("Transmisión de Datos y Redes de Computadores (Especialidadtecnologías de la Información)")) {
					List<String> groups = (List<String>)subject.get("groups");
					assert groups != null;
					assert groups.size() == 4;
					found = true;
					break;
				}
			}
			assert found : "Subject not found in the list";

		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode());
			System.err.println("Response Body: " + e.getResponseBodyAsString());
			throw e; // Re-lanzar la excepción para que el test falle
		}
	}

	// Test if calling -> localhost:8090/schedule-consumer/validate-subscription with a SubscriptionDTO in the body is valid.
	// The first is going to be invalid, the second is going to be valid.
	@Test
	void testValidateSubscription() {
		SubscriptionDTO subscription = new SubscriptionDTO();
		subscription.setGrade("Grado en Ingeniería Informática");
		subscription.setSubject("Ingeniería de Servidores");
		subscription.setGroup("A");

		try {
			Boolean isValid = webClientBuilder.build()
					.post()
					.uri(BASE_URL + "/validate-subscription")
					.header("X-Api-Key", apiKey) // Añadir la cabecera de la API Key
					.bodyValue(subscription)
					.retrieve()
					.bodyToMono(Boolean.class)
					.block();

			assert isValid == true;

			subscription.setGroup("Z");
			isValid = webClientBuilder.build()
					.post()
					.uri(BASE_URL + "/validate-subscription")
					.header("X-Api-Key", apiKey) // Añadir la cabecera de la API Key
					.bodyValue(subscription)
					.retrieve()
					.bodyToMono(Boolean.class)
					.block();

			assert isValid == false;

		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode());
			System.err.println("Response Body: " + e.getResponseBodyAsString());
			throw e; // Re-lanzar la excepción para que el test falle
		}
	}

}
