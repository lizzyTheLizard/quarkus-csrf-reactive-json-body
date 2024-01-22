package org.acme;

import io.quarkus.csrf.reactive.runtime.CsrfRequestResponseReactiveFilter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/service")
public class UserNameResource {

  @Inject
  Template csrfToken;

  @GET
  @Path("/csrfTokenForm")
  @Produces(MediaType.TEXT_HTML)
  public TemplateInstance getCsrfTokenForm() {
    return csrfToken.instance();
  }

  @POST
  @Path("/csrfTokenForm")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> postCsrfTokenForm(@FormParam("name") String userName) {
    return Uni.createFrom().item(userName);
  }

  @POST
  @Path("/csrfTokenPost")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> postJson() {
    return Uni.createFrom().item("no user");
  }


  @POST
  @Path("/csrfTokenPostBody")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> postJson(User user) {
    return Uni.createFrom().item(user.name);
  }

  public static class User {
    private String name;
    public String getName() {
      return this.name;
    }
    public void setName(String name) {
      this.name = name;
    }
  }
}