package org.parasol.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.parasol.ai.ClaimService;
import org.parasol.model.ClaimBotQuery;
import org.parasol.model.ClaimBotQueryResponse;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnError;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocketClient;
import io.quarkus.websockets.next.WebSocketClientConnection;
import io.quarkus.websockets.next.WebSocketConnector;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@QuarkusTest
class ClaimWebsocketChatBotTests {
	private static final String CLAIM = "This is the claim details";
	private static final String QUERY = "Should I approve this claim?";
	private static final LocalDate INCEPTION_DATE = LocalDate.of(1954, 9, 30);
	private static final List<String> RESPONSE = List.of("You", "should", "not", "approve", "this", "claim");
	private static final ArgumentMatcher<ClaimBotQuery> CHAT_SERVICE_MATCHER = query ->
			Objects.nonNull(query) &&
				(query.claimId() == 1L) &&
				QUERY.equals(query.query()) &&
				CLAIM.equals(query.claim());

	@InjectMock
	ClaimService claimService;

	@TestHTTPResource("/")
	URI claimChatBotRootUri;

	@Inject
	WebSocketConnector<ClientEndpoint> connector;

	@BeforeEach
	void beforeEach() {
		ClientEndpoint.MESSAGES.clear();
	}

	@Test
	void chatBotWorks() {
		// A Multi which will return our response with a 0.5 second delay between each item
		var delayedMulti = Multi.createFrom().iterable(RESPONSE)
			.onItem().call(() -> Uni.createFrom().nullItem().onItem().delayIt().by(Duration.ofMillis(500)));

		// Set up our AI mock
		when(this.claimService.chat(argThat(CHAT_SERVICE_MATCHER)))
			.thenReturn(delayedMulti);

		// Create a WebSocket connection and wait for the connection to establish
		var connection = connectClient();

		// Send our query
		connection.sendTextAndAwait(new ClaimBotQuery(1, CLAIM, QUERY, INCEPTION_DATE));

		// Wait for the server to respond with what we expect
		await()
			.atMost(Duration.ofMinutes(5))
			.until(() -> ClientEndpoint.MESSAGES.size() == RESPONSE.size());

		// Verify the messages are what we expected
		assertThat(ClientEndpoint.MESSAGES)
			.hasSameElementsAs(RESPONSE);

		// Close the connection
		connection.closeAndAwait();

		// Verify the AI chat was called with the correct parameters
		verify(this.claimService).chat(argThat(CHAT_SERVICE_MATCHER));
		verifyNoMoreInteractions(this.claimService);
	}

	@Test
	void chatBotHandlesError() {
		var error = new IllegalArgumentException("Something bad happened");

		// Set up mock to throw an error
		when(this.claimService.chat(argThat(CHAT_SERVICE_MATCHER)))
			.thenReturn(Multi.createFrom().failure(error));

		// Create a WebSocket connection and wait for the connection to establish
		var connection = connectClient();

		// Send our query
		connection.sendTextAndAwait(new ClaimBotQuery(1, CLAIM, QUERY, INCEPTION_DATE));

		// Wait for the server to respond with the error we expect
		await()
			.atMost(Duration.ofMinutes(5))
			.until(() -> ClientEndpoint.MESSAGES.size() == 1);

		assertThat(ClientEndpoint.MESSAGES)
			.singleElement()
			.isEqualTo("Error occurred during chat: %s".formatted(error.getMessage()));

		// Close the connection
		connection.closeAndAwait();

		// Verify the AI chat was called with the correct parameters
		verify(this.claimService).chat(argThat(CHAT_SERVICE_MATCHER));
		verifyNoMoreInteractions(this.claimService);
	}

	private WebSocketClientConnection connectClient() {
		var connection = this.connector
			.baseUri(this.claimChatBotRootUri)
			.connectAndAwait();

		waitForClientToStart();

		return connection;
	}

	private static void waitForClientToStart() {
		await()
			.atMost(Duration.ofMinutes(5))
			.until(() -> "CONNECT".equals(ClientEndpoint.MESSAGES.poll()));
	}

	@WebSocketClient(path = "/ws/query", clientId = "c1")
	static class ClientEndpoint {
		private final Logger logger = Logger.getLogger(ClientEndpoint.class);
		static final BlockingQueue<String> MESSAGES = new LinkedBlockingDeque<>();

		@OnOpen
		void open(WebSocketClientConnection connection) {
			this.logger.infof("[CLIENT] Opening endpoint %s", connection.id());
			MESSAGES.offer("CONNECT");
		}

		@OnTextMessage
		void textMessage(ClaimBotQueryResponse message) {
			this.logger.infof("[CLIENT] Got message: %s", message.token());
			MESSAGES.offer(message.token());
		}

		@OnError
		void error(Throwable error) {
			this.logger.errorf(error, "[CLIENT] Got error: %s", error.getMessage());
		}

		@OnClose
		void close(CloseReason closeReason, WebSocketClientConnection connection) {
			this.logger.infof("[CLIENT] Closing endpoint %s: %s: %s", connection.id(), closeReason.getCode(), closeReason.getMessage());
		}
	}
}