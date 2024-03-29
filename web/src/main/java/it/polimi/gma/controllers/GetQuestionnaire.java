package it.polimi.gma.controllers;

import it.polimi.gma.entities.Questionnaire;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "GetQuestionnaire", value = "/GetQuestionnaire")
public class GetQuestionnaire extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        Questionnaire questionnaire = questionnaireService.getQuestionnaireOfTheDay();
        ctx.setVariable("questions", questionnaire.getQuestions());

        templateEngine.process("/WEB-INF/QuestionnaireMarketing.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String next = request.getParameter("next");
        String previous = request.getParameter("previous");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        String filename = null;

        // get answers
        Enumeration<String> inputs = request.getParameterNames();
        Map<Integer, String> answers = new HashMap<>();
        while (inputs.hasMoreElements()) {
            String param = inputs.nextElement();
            if (!param.contains("next") && !param.contains("previous")) {
                answers.put(Integer.parseInt(param), request.getParameter(param).trim());
            }
        }

        // if 'next' has been clicked
        // now we are in the marketing section and must serve the statistical section
        if (next != null) {
            session.setAttribute("marketing", answers);

            ctx.setVariable("questions", questionnaireService.getStatisticalQuestions());

            filename = "Statistical";
        }
        // if 'previous' has been clicked
        // now we are in the statistical section and must serve the marketing section
        else if (previous != null) {
            session.setAttribute("statistical", answers);

            Questionnaire questionnaire = questionnaireService.getQuestionnaireOfTheDay();
            ctx.setVariable("questions", questionnaire.getQuestions());

            filename = "Marketing";
        }

        templateEngine.process("/WEB-INF/Questionnaire" + filename + ".html", ctx, response.getWriter());
    }
}
