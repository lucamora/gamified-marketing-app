package it.polimi.gma.controllers;

import it.polimi.gma.entities.Questionnaire;
import it.polimi.gma.entities.Section;
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
        ctx.setVariable("questions", questionnaire.filterQuestions(Section.MARKETING));

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

        // if 'next' has been clicked
        // now we are in the marketing section and must serve the statistical section
        if (next != null) {
            Enumeration<String> inputs = request.getParameterNames();
            Map<Integer, String> answers = new HashMap<>();
            while (inputs.hasMoreElements()) {
                String param = inputs.nextElement();
                if (!param.contains("next")) {
                    answers.put(Integer.parseInt(param), request.getParameter(param).trim());
                }
            }
            session.setAttribute("answers", answers);

            filename = "Statistical";
        }
        // if 'previous' has been clicked
        // now we are in the statistical section and must serve the marketing section
        else if (previous != null) {
            session.setAttribute("age", request.getParameter("age"));
            session.setAttribute("sex", request.getParameter("sex"));
            session.setAttribute("expertise", request.getParameter("expertise"));

            Questionnaire questionnaire = questionnaireService.getQuestionnaireOfTheDay();
            ctx.setVariable("questions", questionnaire.filterQuestions(Section.MARKETING));

            filename = "Marketing";
        }

        templateEngine.process("/WEB-INF/Questionnaire" + filename + ".html", ctx, response.getWriter());
    }
}
