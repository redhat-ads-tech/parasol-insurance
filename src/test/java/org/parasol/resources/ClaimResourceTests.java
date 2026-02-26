package org.parasol.resources;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.parasol.model.Claim;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
class ClaimResourceTests {
	@Test
	void getAllNoneFound() {
		PanacheMock.mock(Claim.class);

		when(Claim.listAll())
			.thenReturn(List.of());

		get("/api/db/claims").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.body("$.size()", is(0));

		PanacheMock.verify(Claim.class).listAll();
		PanacheMock.verifyNoMoreInteractions(Claim.class);
	}

	@Test
	void getAllSomeFound() {
		PanacheMock.mock(Claim.class);
		when(Claim.listAll())
			.thenReturn(List.of(createClaim()));

		var claims = get("/api/db/claims").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.extract().body()
			.jsonPath().getList(".", Claim.class);

		assertThat(claims)
			.isNotNull()
			.singleElement()
			.usingRecursiveComparison()
			.isEqualTo(createClaim());

		PanacheMock.verify(Claim.class).listAll();
		PanacheMock.verifyNoMoreInteractions(Claim.class);
	}

	@Test
	void getOneNotFound() {
		PanacheMock.mock(Claim.class);
		when(Claim.findById(1))
			.thenReturn(null);

		get("/api/db/claims/{id}", 1).then()
			.statusCode(Status.NO_CONTENT.getStatusCode())
			.contentType(ContentType.JSON)
			.body(blankOrNullString());

		PanacheMock.verify(Claim.class).findById(1);
		PanacheMock.verifyNoMoreInteractions(Claim.class);
	}

	@Test
	void getOneFound() {
		PanacheMock.mock(Claim.class);
		when(Claim.findById(1))
			.thenReturn(createClaim());

		var claim = get("/api/db/claims/{id}", 1).then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.extract().as(Claim.class);

		assertThat(claim)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(createClaim());

		PanacheMock.verify(Claim.class).findById(1);
		PanacheMock.verifyNoMoreInteractions(Claim.class);
	}

	private static Claim createClaim() {
		var claim = new Claim();
		claim.setClaimNumber("001");
		claim.setCategory("Auto");
		claim.setPolicyNumber("123");
		claim.setClientName("client");
		claim.setSubject("collision");
		claim.setBody("body");
		claim.setSummary("Car was damaged in accident");
		claim.setLocation("driveway");
		claim.setTime("afternoon");
		claim.setSentiment("Very bad");

		return claim;
	}
}