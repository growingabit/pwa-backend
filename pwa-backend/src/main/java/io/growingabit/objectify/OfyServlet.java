package io.growingabit.objectify;

import javax.servlet.http.HttpServlet;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.backoffice.model.Invitation;

public class OfyServlet extends HttpServlet {

  private static final long serialVersionUID = -4243709944154393569L;

  static {
    ObjectifyService.factory().register(Invitation.class);
  }
}
