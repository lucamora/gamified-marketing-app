package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
import it.polimi.gma.exceptions.EmptyAnswerException;
import it.polimi.gma.exceptions.OffensiveWordException;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.services.UserService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

@WebServlet(name = "SubmitQuestionnaire", value = "/SubmitQuestionnaire")
public class SubmitQuestionnaire extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @EJB(name = "UserServiceEJB")
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // get current user from session
        User user = (User)session.getAttribute("user");

        // get marketing questions from session
        @SuppressWarnings("unchecked")
        Map<Integer, String> answers = (Map<Integer, String>)session.getAttribute("marketing");

        // get statistical questions from form parameters
        Enumeration<String> inputs = request.getParameterNames();
        while (inputs.hasMoreElements()) {
            String param = inputs.nextElement();
            if (!param.contains("submit")) {
                answers.put(Integer.parseInt(param), request.getParameter(param).trim());
            }
        }

        String status = "";
        try {
            questionnaireService.submit(answers, user);
        }
        catch (EmptyAnswerException e) {
            // some marketing question does not have a response
            status = "invalid";
        }
        catch (OffensiveWordException e) {
            // user inserted an offensive word in an answer
            status = "blocked";

            // block user
            userService.blockUser(user);

            // update user in session
            session.setAttribute("user", user);
        }

        // clear session attributes after submission
        session.removeAttribute("marketing");
        session.removeAttribute("statistical");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("status", status);

        templateEngine.process("/WEB-INF/PostSubmission.html", ctx, response.getWriter());
    }
}
