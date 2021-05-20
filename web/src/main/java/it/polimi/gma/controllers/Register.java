package it.polimi.gma.controllers;

import it.polimi.gma.exceptions.AlreadyRegisteredException;
import it.polimi.gma.exceptions.CredentialsException;
import it.polimi.gma.services.UserService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "Register", value = "/Registration")
public class Register extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "UserServiceEJB")
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get fields
        String email = request.getParameter("email");
        String usr = request.getParameter("username");
        String pwd = request.getParameter("password");
        String confpwd = request.getParameter("confirmPassword");
        if (email == null || usr == null || pwd == null || confpwd == null) {
            invalidCredentials("Missing or empty credential value", request, response);
            return;
        }

        email = email.trim();
        usr = usr.trim();
        pwd = pwd.trim();
        confpwd = confpwd.trim();
        if (email.isEmpty() || usr.isEmpty() || pwd.isEmpty() || confpwd.isEmpty()) {
            invalidCredentials("Missing or empty credential value", request, response);
            return;
        }

        if (!pwd.equals(confpwd)) {
            invalidCredentials("Passwords do not match", request, response);
            return;
        }

        try {
            userService.registerUser(email, usr, pwd);
        }
        catch (CredentialsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not register user");
            return;
        }
        catch (AlreadyRegisteredException e) {
            invalidCredentials("Username already registered", request, response);
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("successMessage", "Registration completed! Please login to use the service");
        templateEngine.process("/index.html", ctx, response.getWriter());
    }

    private void invalidCredentials(String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("errorMessage", message);
        templateEngine.process("/registration.html", ctx, response.getWriter());
    }
}
