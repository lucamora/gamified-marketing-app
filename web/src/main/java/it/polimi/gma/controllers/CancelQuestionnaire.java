package it.polimi.gma.controllers;

import it.polimi.gma.entities.User;
import it.polimi.gma.services.QuestionnaireService;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "CancelQuestionnaire", value = "/CancelQuestionnaire")
public class CancelQuestionnaire extends HttpServlet {

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // check if there is a questionnaire to cancel
        if (session.getAttribute("marketing") != null) {
            // clear session attributes after cancellation
            session.removeAttribute("marketing");
            session.removeAttribute("statistical");

            // get current user from session
            User user = (User)session.getAttribute("user");

            // cancel questionnaire
            questionnaireService.cancel(user);
        }

        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }
}
