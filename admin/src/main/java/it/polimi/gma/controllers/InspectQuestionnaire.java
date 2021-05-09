package it.polimi.gma.controllers;

import it.polimi.gma.entities.Questionnaire;
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

@WebServlet(name = "InspectQuestionnaire", value = "/InspectQuestionnaire")
public class InspectQuestionnaire extends HttpServlet {
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

        int questionnaireId;
        try {
            questionnaireId = Integer.parseInt(request.getParameter("questionnaire"));
        }
        catch (Exception e) {
            questionnaireId = -1;
        }

        Questionnaire questionnaire = null;
        List<User> usersSubmitted = null;
        List<User> usersCancelled = null;
        if (questionnaireId > -1) {
            questionnaire = questionnaireService.getQuestionnaireById(questionnaireId);
            usersSubmitted = questionnaireService.getUsersSubmitted(questionnaire);
            usersCancelled = questionnaireService.getUsersCancelled(questionnaire);
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("questionnaires", questionnaireService.getPastQuestionnaires());
        ctx.setVariable("questionnaire", questionnaire);
        ctx.setVariable("usersSubmitted", usersSubmitted);
        ctx.setVariable("usersCancelled", usersCancelled);

        templateEngine.process("/WEB-INF/InspectQuestionnaire.html", ctx, response.getWriter());
    }
}
