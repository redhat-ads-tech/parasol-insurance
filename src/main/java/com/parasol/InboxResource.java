package com.parasol;

import java.util.List;

import com.parasol.model.Email;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/inbox")
@Produces(MediaType.APPLICATION_JSON)
public class InboxResource {

    private final EmailStore store;

    public InboxResource(EmailStore store) {
        this.store = store;
    }

    @GET
    public List<Email> getEmails(@QueryParam("after") Long after) {
        if (after != null) {
            return store.after(after);
        }
        return store.all();
    }
}
