package org.parasol.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.parasol.model.Claim;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkiverse.quinoa.testing.QuinoaTestProfiles;

@QuarkusTest
@TestProfile(QuinoaTestProfiles.Enable.class)
@WithPlaywright
public class ClaimsListPageTests extends PlaywrightTests {
	private static final int NB_CLAIMS = 6;

	@Test
	void pageLoads() {
		var page = loadPage();

		PlaywrightAssertions.assertThat(page)
			.hasTitle("Claims List");
	}

	@Test
	void correctTable() {
		var table = getAndVerifyClaimsTable(NB_CLAIMS);
		var tableColumns = table.getByRole(AriaRole.COLUMNHEADER).all();

		assertThat(tableColumns)
			.isNotNull()
			.hasSize(5)
			.extracting(Locator::textContent)
			.containsExactly(
"Claim Number",
				"Category",
				"Client Name",
				"Policy Number",
				"Status"
			);

		var rows = getTableBodyRows(table);
		assertThat(rows)
			.isNotNull()
			.hasSize(NB_CLAIMS);

		var claim = Claim.<Claim>findById(1L);
		var firstRow = rows.get(0).getByRole(AriaRole.GRIDCELL).all();
		assertThat(firstRow)
			.isNotNull()
			.hasSize(5);

		assertThat(firstRow.get(0))
			.isNotNull()
			.extracting(
				Locator::textContent,
				l -> l.getByRole(AriaRole.LINK).getAttribute("href")
			)
			.containsExactly(
				claim.claimNumber,
				"/ClaimDetail/%d".formatted(claim.id)
			);

		assertThat(firstRow.get(1))
			.isNotNull()
			.extracting(Locator::textContent)
			.isEqualTo(claim.category);

		assertThat(firstRow.get(2))
			.isNotNull()
			.extracting(Locator::textContent)
			.isEqualTo(claim.clientName);

		assertThat(firstRow.get(3))
			.isNotNull()
			.extracting(Locator::textContent)
			.isEqualTo(claim.policyNumber);

		assertThat(firstRow.get(4))
			.isNotNull()
			.extracting(Locator::textContent)
			.isEqualTo(claim.status);
	}

	private List<Locator> getTableBodyRows(Locator table) {
		// Rowgroup 1 is the header row
		// Rowgroup 2 is the body rows
		var rowGroups = table.getByRole(AriaRole.ROWGROUP).all();
		assertThat(rowGroups)
			.isNotNull()
			.hasSize(2);

		return rowGroups.get(1).getByRole(AriaRole.ROW).all();
	}

	private Locator getAndVerifyClaimsTable(Page page, int expectedNumRows) {
		var table = page.getByRole(AriaRole.GRID);
		assertThat(table).isNotNull();

		var tableBodyRows = getTableBodyRows(table);
		assertThat(tableBodyRows)
			.isNotNull()
			.hasSize(expectedNumRows);

		return table;
	}

	private Locator getAndVerifyClaimsTable(int expectedNumRows) {
		return getAndVerifyClaimsTable(loadPage(), expectedNumRows);
	}

	private Page loadPage() {
		return loadPage("ClaimsList");
	}
}
