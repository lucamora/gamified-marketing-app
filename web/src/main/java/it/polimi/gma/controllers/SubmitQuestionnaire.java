package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
import it.polimi.gma.services.QuestionnaireService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import utils.ThymeleafFactory;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "SubmitQuestionnaire", value = "/SubmitQuestionnaire")
public class SubmitQuestionnaire extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if user is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        // TODO: save questionnaire
        // get marketing questions from session
        @SuppressWarnings("unchecked")
        Map<Integer, String> answers = (Map<Integer, String>)session.getAttribute("answers");

        // check if marketing questions have responses
        boolean invalid = false;
        for (String ans : answers.values()) {
            if (ans.isEmpty()) {
                invalid = true;
                break;
            }
        }

        // get statistical questions from form parameters
        answers.put(1, request.getParameter("age"));
        answers.put(2, request.getParameter("sex"));
        answers.put(3, request.getParameter("expertise"));

        questionnaireService.submit(answers, (User)session.getAttribute("user"));

        // clear session attributes after submission
        session.removeAttribute("answers");
        session.removeAttribute("age");
        session.removeAttribute("sex");
        session.removeAttribute("expertise");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("invalid", invalid);

        templateEngine.process("/WEB-INF/PostSubmission.html", ctx, response.getWriter());
    }
}
