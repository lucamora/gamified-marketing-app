package it.polimi.gma.controllers;

import it.polimi.gma.entities.Product;
import it.polimi.gma.entities.User;
import it.polimi.gma.services.ProductService;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "GoToHomePage", value = "/Home")
public class GoToHomePage extends HttpServlet {
    @EJB(name = "ProductServiceEJB")
    private ProductService productService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        //Product product = productService.getProductOfTheDay();

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello World!</h1>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
