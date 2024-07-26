package org.parasol.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ClaimTests {
	@Test
	void getClaimNumberFound() {
		assertThat(Claim.getClaimNumber(1))
			.isNotNull()
			.get()
			.isEqualTo("CLM195501");
	}

	@Test
	void getClaimNumberNotFound() {
		assertThat(Claim.getClaimNumber(-1))
			.isNotNull()
			.isNotPresent();
	}
}