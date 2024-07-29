package org.parasol.ai;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.parasol.model.Claim;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class NotificationService {
	static final String INVALID_STATUS = "Status \"%s\" is not valid";
	static final String NOTIFICATION_SUCCESS = "%s (claim number %s) has been notified of status update \"%s\"";
	static final String NOTIFICATION_NO_CLAIMANT_FOUND = "No claim record found in the database for the given claim";
	static final String MESSAGE_FROM = "noreply@parasol.com";
	static final String MESSAGE_SUBJECT = "Update to your claim";
	static final String MESSAGE_BODY = """
		Dear %s,
		
		This is an official communication from the Parasol Insurance Claims Department. We wanted to let you know that your claim (claim # %s) has changed status to "%s".
		
		Sincerely,
		Parasoft Insurance Claims Department
		
		--------------------------------------------
		Please note this is an unmonitored email box.
		""";

	@Inject
	Mailer mailer;

	@Tool("update claim status")
	@Transactional
	public String updateClaimStatus(long claimId, String status) {
		return Optional.ofNullable(status)
			.filter(s -> s.trim().length() > 2)
			.map(s -> updateStatus(claimId, s))
			.orElse(INVALID_STATUS.formatted(status));
	}

	private String updateStatus(long claimId, String status) {
		return Claim.<Claim>findByIdOptional(claimId)
			.map(claim -> updateStatus(claim, status))
			.orElse(NOTIFICATION_NO_CLAIMANT_FOUND);
	}

	private String updateStatus(Claim claim, String status) {
		claim.status = status.trim().substring(0, 1).toUpperCase() + status.trim().substring(1);
		Claim.persist(claim);

		this.mailer.send(
			Mail.withText(claim.emailAddress, MESSAGE_SUBJECT, MESSAGE_BODY.formatted(claim.clientName, claim.claimNumber, claim.status))
				.setFrom(MESSAGE_FROM)
		);

		return NOTIFICATION_SUCCESS.formatted(claim.emailAddress, claim.claimNumber, claim.status);
	}
}
