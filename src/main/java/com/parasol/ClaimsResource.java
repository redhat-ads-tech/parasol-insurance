package com.parasol;

import java.util.List;

import com.parasol.model.Claim;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/claims")
@Produces(MediaType.APPLICATION_JSON)
public class ClaimsResource {

    @GET
    public List<Claim> getAllClaims() {
        return Claim.listAll(); // NOSONAR
    }

    @GET
    @Path("/{claimNumber}")
    public Response getClaim(@PathParam("claimNumber") String claimNumber) {
        Claim claim = Claim.find("claimNumber", claimNumber).firstResult(); // NOSONAR
        if (claim != null) {
            return Response.ok(claim).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
