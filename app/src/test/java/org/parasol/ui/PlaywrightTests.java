package org.parasol.ui;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import io.quarkiverse.playwright.InjectPlaywright;

public abstract class PlaywrightTests {
	@InjectPlaywright
	protected BrowserContext context;

	@ConfigProperty(name = "quarkus.http.test-port")
  private int quarkusPort;

	protected String getUrl(String subPage) {
		return "http://localhost:%d/%s".formatted(this.quarkusPort, subPage);
	}

	protected Page loadPage(String subPage) {
		var page = this.context.newPage();
		var response = page.navigate(getUrl(subPage));

		assertThat(response)
			.isNotNull()
			.extracting(Response::status)
			.isEqualTo(Status.OK.getStatusCode());

		page.waitForLoadState(LoadState.NETWORKIDLE);

		return page;
	}
}
