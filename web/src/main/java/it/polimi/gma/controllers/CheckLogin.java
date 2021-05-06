package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
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

@WebServlet(name = "CheckLogin", value = "/CheckLogin")
public class CheckLogin extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "UserServiceEJB")
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // obtain params
        String usr = null;
        String pwd = null;
        try {
            usr = request.getParameter("username");
            pwd = request.getParameter("password");
            if (usr == null || pwd == null || usr.isEmpty() || pwd.isEmpty()) {
                throw new Exception("Missing or empty credential value");
            }

        } catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
            invalidCredentials(request, response);
            return;
        }

        User user;
        try {
            // query db to authenticate for user
            user = userService.checkCredentials(usr, pwd);
        } catch (CredentialsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
            return;
        }

        // If the user exists, add info to the session and go to home page, otherwise
        // show login page with error message

        if (user == null) {
            invalidCredentials(request, response);
            return;
        }

        // store in the db that the user has logged in
        userService.saveLogin(user);

        request.getSession().setAttribute("user", user);
        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }

    private void invalidCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("errorMessage", "Incorrect username or password");
        templateEngine.process("/index.html", ctx, response.getWriter());
    }
}
