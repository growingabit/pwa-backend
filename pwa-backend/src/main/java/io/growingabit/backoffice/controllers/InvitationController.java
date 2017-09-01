package io.growingabit.backoffice.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.googlecode.objectify.SaveException;

import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;

@Path("backoffice/invitation")
public class InvitationController {

    private InvitationDao dao = new InvitationDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(dao.findAll()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Invitation invitation) {
        try {
            dao.persist(invitation);
            return Response.ok(invitation).build();
        } catch (SaveException e) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    public String createInvitationCode(Invitation invitation) {
        return invitation.getId().toString().substring(0, 6);
    }
}
