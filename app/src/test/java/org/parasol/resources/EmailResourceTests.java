package org.parasol.resources;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.parasol.ai.EmailService;
import org.parasol.model.Email;
import org.parasol.model.EmailResponse;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
class EmailResourceTests {
	@InjectMock
	EmailService emailService;

	@Test
	void getResponse() {
		var response = new EmailResponse("Re: Claim Submission - Claim details", "Dear [Customer], thank you!");

		when(this.emailService.chat("Claim details"))
			.thenReturn(response);

		var httpResponse = given()
			.contentType(ContentType.JSON)
			.body(new Email("Claim details"))
			.when()
			.post("/api/email").then()
			.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
			.extract().as(EmailResponse.class);

		assertThat(httpResponse)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(this.emailService).chat("Claim details");
		verifyNoMoreInteractions(this.emailService);
	}
}