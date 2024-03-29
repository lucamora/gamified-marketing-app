package it.polimi.gma.controllers;

import it.polimi.gma.entities.Product;
import it.polimi.gma.exceptions.AlreadyCreatedException;
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
import java.util.Date;

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
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("date", new Date());

        templateEngine.process("/WEB-INF/CreateProduct.html", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product name");
            return;
        }

        name = name.trim();
        if (name.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product name");
            return;
        }

        Part imageFile = request.getPart("image");
        byte[] image = ImageReader.read(imageFile.getInputStream());

        Product product;
        try {
            product = productService.createProduct(name, image);
        }
        catch (AlreadyCreatedException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("product", product);

        templateEngine.process("/WEB-INF/PostProductCreation.html", ctx, response.getWriter());
    }
}
