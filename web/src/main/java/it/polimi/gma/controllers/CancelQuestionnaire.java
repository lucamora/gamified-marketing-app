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
        // check if user is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        // get current user from session
        User user = (User)session.getAttribute("user");

        if (!user.isBlocked() && questionnaireService.checkIfCanSubmit(user)) {
            questionnaireService.cancel(user);
        }

        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }
}
