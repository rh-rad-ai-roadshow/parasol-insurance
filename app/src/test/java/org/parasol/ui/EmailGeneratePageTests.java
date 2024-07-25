package org.parasol.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkiverse.quinoa.testing.QuinoaTestProfiles;

@QuarkusTest
@TestProfile(QuinoaTestProfiles.Enable.class)
@WithPlaywright
public class EmailGeneratePageTests extends PlaywrightTests {
	@Test
	void pageLoads() {
		var page = loadPage();

		PlaywrightAssertions.assertThat(page)
			.hasTitle("Email Generate");

		var emailContentField = page.getByPlaceholder("Enter customer email content here");
		PlaywrightAssertions.assertThat(emailContentField).isVisible();
		emailContentField.fill("This is my claim details");

		var submitButton = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Submit"));
		PlaywrightAssertions.assertThat(submitButton).isVisible();
		submitButton.click();

		await()
			.atMost(Duration.ofMinutes(5))
			.until(() -> getResponseMessage(page).isVisible() && getResponseSubject(page).isVisible());

		var subject = getResponseSubject(page);
		PlaywrightAssertions.assertThat(subject).isVisible();
		assertThat(subject.textContent())
			.isNotNull()
			.startsWith("Subject: ")
			.hasSizeGreaterThan("Subject: ".length());

		var message = getResponseMessage(page);
		PlaywrightAssertions.assertThat(message).isVisible();
		assertThat(message.textContent())
			.isNotNull()
			.isNotEmpty();
	}

	private Page loadPage() {
		return loadPage("EmailGenerate");
	}

	private static Locator getResponseSubject(Page page) {
		return page.locator("#email-response-subject");
	}

	private static Locator getResponseMessage(Page page) {
		return page.locator("#email-response-message");
	}
}
