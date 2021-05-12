package it.polimi.gma.filters;

import it.polimi.gma.entities.User;
import it.polimi.gma.services.QuestionnaireService;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CheckCanDoQuestionnaire")
public class CheckCanDoQuestionnaire implements Filter {

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        User user = (User)req.getSession().getAttribute("user");

        // user can submit/cancel if is not blocked and if has not already submitted
        boolean canSubmit = questionnaireService.checkNotSubmitted(user);
        if (user.isBlocked() || !canSubmit) {
            res.sendRedirect(req.getServletContext().getContextPath() + "/Home");
            return;
        }

        chain.doFilter(request, response);
    }
}
