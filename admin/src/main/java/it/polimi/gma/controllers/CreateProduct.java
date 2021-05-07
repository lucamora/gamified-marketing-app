package it.polimi.gma.controllers;

import it.polimi.gma.exceptions.AlreadyCreatedException;
import it.polimi.gma.exceptions.InvalidDateException;
import it.polimi.gma.services.ProductService;
import it.polimi.gma.utils.ImageReader;
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

@WebServlet(name = "CreateProduct", value = "/CreateProduct")
@MultipartConfig
public class CreateProduct extends HttpServlet {
    private TemplateEngine templateEngine;

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

        templateEngine.process("/WEB-INF/CreateProduct.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check if admin is logged in
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("admin") == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/index.html");
            return;
        }

        String name = request.getParameter("name");
        String dateStr = request.getParameter("date");

        if (name == null || dateStr == null || name.isEmpty() || dateStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product parameters");
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product parameters");
            return;
        }

        Part imageFile = request.getPart("image");
        byte[] image = ImageReader.read(imageFile.getInputStream());

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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product parameters");
            return;
        }

        try {
            productService.createProduct(name, date, image, questions);
        }
        catch (AlreadyCreatedException | InvalidDateException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("name", name);
        ctx.setVariable("date", date);
        ctx.setVariable("image", image);
        ctx.setVariable("questions", questions);

        templateEngine.process("/WEB-INF/PostCreation.html", ctx, response.getWriter());
    }
}
