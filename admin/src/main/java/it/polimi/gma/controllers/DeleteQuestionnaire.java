package it.polimi.gma.controllers;

import it.polimi.gma.entities.Questionnaire;
import it.polimi.gma.exceptions.InexistentQuestionnaire;
import it.polimi.gma.exceptions.InvalidDateException;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "DeleteQuestionnaire", value = "/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if admin is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("admin") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("questionnaires", questionnaireService.getPastQuestionnaire());

        templateEngine.process("/WEB-INF/DeleteQuestionnaire.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if admin is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("admin") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        int questionnaireId;
        try {
            questionnaireId = Integer.parseInt(request.getParameter("questionnaire"));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionnaire id");
            return;
        }

        // get and delete questionnaire
        Questionnaire questionnaire;
        try {
            questionnaire = questionnaireService.deleteQuestionnaire(questionnaireId);
        } catch (InvalidDateException | InexistentQuestionnaire e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("name", questionnaire.getProduct().getName());
        ctx.setVariable("date", questionnaire.getProduct().getDate());

        templateEngine.process("/WEB-INF/PostDeletion.html", ctx, response.getWriter());
    }
}
