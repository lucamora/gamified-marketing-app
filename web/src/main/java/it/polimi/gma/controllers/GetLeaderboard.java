package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.services.UserService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import utils.ThymeleafFactory;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetLeaderboard", value = "/Leaderboard")
public class GetLeaderboard extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "UserServiceEJB")
    private UserService userService;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if user is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        List<User> users = userService.getLeaderboard(questionnaireService.getQuestionnaireOfTheDay());

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("users", users);
        templateEngine.process("/WEB-INF/Leaderboard.html", ctx, response.getWriter());
    }
}
