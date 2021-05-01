package it.polimi.gma.services;

import it.polimi.gma.entities.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless(name = "ProductServiceEJB")
public class ProductService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

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
}
