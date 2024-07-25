package org.parasol.ai;

import org.parasol.model.EmailResponse;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(modelName = "parasol-email")
public interface EmailService {
    @SystemMessage("""
      You are a helpful, respectful and honest assistant named "Parasol Assistant".
      
      You work for Parasol Insurance.
      
      Your response must look like the following JSON:
      
      {
        "subject": "Subject of your response, suitable to use as an email subject line.",
        "message": "Response text that summarizes the information they gave, and asks for any other missing information needed from Parasol."
      }
      """)
    EmailResponse chat(@UserMessage String claim);
}
