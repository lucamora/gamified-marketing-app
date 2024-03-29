package it.polimi.gma.controllers;

import it.polimi.gma.entities.Product;
import it.polimi.gma.entities.User;
import it.polimi.gma.services.ProductService;
import it.polimi.gma.services.QuestionnaireService;
import it.polimi.gma.utils.ThymeleafFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GoToHomePage", value = "/Home")
public class GoToHomePage extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "ProductServiceEJB")
    private ProductService productService;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @Override
    public void init() throws ServletException {
        this.templateEngine = ThymeleafFactory.create(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User)session.getAttribute("user");
        boolean canSubmit = questionnaireService.checkUserCanSubmit(user);

        Product product = productService.getProductOfTheDay();

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("product", product);
        ctx.setVariable("userBlocked", user.isBlocked());
        ctx.setVariable("canSubmit", canSubmit);

        templateEngine.process("/WEB-INF/Home.html", ctx, response.getWriter());
    }
}
