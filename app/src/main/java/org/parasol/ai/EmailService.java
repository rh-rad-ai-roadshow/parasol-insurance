package org.parasol.ai;

import org.parasol.model.EmailResponse;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(modelName = "parasol-email")
public interface EmailService {
    @SystemMessage("""
        You are a helpful, respectful and honest assistant named \"Parasol Assistant\". You work for Parasol Insurance. You will be given a message from a customer making an insurance claim.
        """
    )
    @UserMessage("""
    Your response must contain the following elements in JSON:
        - the 'subject' key set to the subject of your response, suitable to use as an email subject line.
        - the 'message' key set to the response text that summarizes the information they gave, and asks for any other missing information needed from Parasol.

        Here is the email from the customer: {{claim}}

        Complete the response: {{{
    """)
    EmailResponse chat(String claim);
}
