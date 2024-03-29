package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetLeaderboard", value = "/Leaderboard")
public class GetLeaderboard extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> users = questionnaireService.getLeaderboard();

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("users", users);
        templateEngine.process("/WEB-INF/Leaderboard.html", ctx, response.getWriter());
    }
}
