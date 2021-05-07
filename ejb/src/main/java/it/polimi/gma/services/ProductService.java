package it.polimi.gma.services;

import it.polimi.gma.entities.Product;
import it.polimi.gma.exceptions.AlreadyCreatedException;
import it.polimi.gma.exceptions.InvalidDateException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Stateless(name = "ProductServiceEJB")
public class ProductService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

    @EJB(name = "QuestionnaireServiceEJB")
    private QuestionnaireService questionnaireService;

    public ProductService() {
    }

    public Product getProductOfTheDay() {
        try {
            return em.createNamedQuery("Product.getOfTheDay", Product.class)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public void createProduct(String name, Date date, byte[] image, List<String> questions) throws AlreadyCreatedException, InvalidDateException {
        // check if the date is valid (current date or a posterior date)
        LocalDate publish = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (publish.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Date should be today or a future date");
        }

        List<Product> products =
                em.createNamedQuery("Product.getByDate", Product.class)
                        .setParameter("date", date)
                        .getResultList();

        // check if a product for the requested date already exist
        if (!products.isEmpty()) {
            throw new AlreadyCreatedException("Product for the day already created");
        }

        Product product = new Product();
        product.setName(name.trim());
        product.setDate(date);
        product.setImage(image);

        questionnaireService.createQuestionnaire(product, questions);

        em.persist(product);
    }
}
