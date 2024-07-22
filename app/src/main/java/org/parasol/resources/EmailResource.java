package org.parasol.resources;

import org.parasol.ai.EmailService;
import org.parasol.model.Email;
import org.parasol.model.EmailResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/api/email")
public class EmailResource {

    @Inject
 EmailService bot;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public EmailResponse getresponse(Email claimEmail) {
        return bot.chat(claimEmail.text);
    }
}
