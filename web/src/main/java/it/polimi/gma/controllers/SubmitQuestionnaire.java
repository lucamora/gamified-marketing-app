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
        // check if user is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        // get current user from session
        User user = (User)session.getAttribute("user");

        if (!checkIfCanSubmit(response, user)) {
            return;
        }

        // get marketing questions from session
        @SuppressWarnings("unchecked")
        Map<Integer, String> answers = (Map<Integer, String>)session.getAttribute("answers");

        // get statistical questions from form parameters
        answers.put(1, request.getParameter("age"));
        answers.put(2, request.getParameter("sex"));
        answers.put(3, request.getParameter("expertise"));

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
        session.removeAttribute("answers");
        session.removeAttribute("age");
        session.removeAttribute("sex");
        session.removeAttribute("expertise");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("status", status);

        templateEngine.process("/WEB-INF/PostSubmission.html", ctx, response.getWriter());
    }

    private boolean checkIfCanSubmit(HttpServletResponse response, User user) throws IOException {
        boolean canSubmit = questionnaireService.checkIfCanSubmit(user);
        if (user.isBlocked() || !canSubmit) {
            response.sendRedirect(getServletContext().getContextPath() + "/Home");
            return false;
        }
        return true;
    }
}
