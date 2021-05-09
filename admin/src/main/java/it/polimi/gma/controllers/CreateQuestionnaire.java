package it.polimi.gma.controllers;

import it.polimi.gma.entities.Product;
import it.polimi.gma.entities.Questionnaire;
import it.polimi.gma.exceptions.AlreadyCreatedException;
import it.polimi.gma.exceptions.InvalidDateException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@WebServlet(name = "CreateQuestionnaire", value = "/CreateQuestionnaire")
public class CreateQuestionnaire extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    @EJB(name = "ProductServiceEJB")
    private ProductService productService;

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

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("date", new Date());
        ctx.setVariable("products", productService.getAllProducts());

        templateEngine.process("/WEB-INF/CreateQuestionnaire.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if admin is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("admin") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        String dateStr = request.getParameter("date");
        if (dateStr == null || dateStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionnaire parameters");
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionnaire parameters");
            return;
        }

        Enumeration<String> inputs = request.getParameterNames();
        List<String> questions = new ArrayList<>();

        while (inputs.hasMoreElements()) {
            String param = inputs.nextElement();
            if (param.startsWith("question_")) {
                String quest = request.getParameter(param);
                if (quest != null && !quest.trim().isEmpty()) {
                    questions.add(quest);
                }
            }
        }

        if (questions.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionnaire parameters");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(request.getParameter("product"));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product");
            return;
        }

        // get product
        Product product = productService.getProductById(productId);

        Questionnaire questionnaire;
        try {
            questionnaire = questionnaireService.createQuestionnaire(product, date, questions);
        } catch (InvalidDateException | AlreadyCreatedException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("questionnaire", questionnaire);

        templateEngine.process("/WEB-INF/PostQuestionnaireCreation.html", ctx, response.getWriter());
    }
}
