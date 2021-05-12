package it.polimi.gma.controllers;

import it.polimi.gma.entities.Administrator;
import it.polimi.gma.exceptions.CredentialsException;
import it.polimi.gma.services.AdministratorService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "CheckLogin", value = "/CheckLogin")
public class CheckLogin extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "AdministratorServiceEJB")
    private AdministratorService administratorService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // obtain params
        String usr = request.getParameter("username");
        String pwd = request.getParameter("password");
        if (usr == null || pwd == null) {
            invalidCredentials(request, response);
            return;
        }

        usr = usr.trim();
        pwd = pwd.trim();
        if (usr.isEmpty() || pwd.isEmpty()) {
            invalidCredentials(request, response);
            return;
        }

        Administrator admin;
        try {
            // query db to authenticate for user
            admin = administratorService.checkCredentials(usr, pwd);
        } catch (CredentialsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
            return;
        }

        // If the admin exists, add info to the session and go to home page, otherwise
        // show login page with error message

        if (admin == null) {
            invalidCredentials(request, response);
            return;
        }

        request.getSession().setAttribute("admin", admin);
        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }

    private void invalidCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("errorMessage", "Incorrect username or password");
        templateEngine.process("/index.html", ctx, response.getWriter());
    }
}
