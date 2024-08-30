package org.parasol.model;

import java.time.LocalDate;

public record ClaimBotQuery(long claimId, String claim, String query, LocalDate inceptionDate) {

}
