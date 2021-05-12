package it.polimi.gma.services;

import it.polimi.gma.entities.Product;
import it.polimi.gma.exceptions.AlreadyCreatedException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public List<Product> getAllProducts() {
        return em.createNamedQuery("Product.getAll", Product.class)
                .getResultList();
    }

    public Product getProductById(int id) {
        return em.find(Product.class, id);
    }

    public Product createProduct(String name, byte[] image) throws AlreadyCreatedException {
        List<Product> products =
                em.createNamedQuery("Product.getByName", Product.class)
                        .setParameter("name", name)
                        .getResultList();

        // check if a product with the same name already exist
        if (!products.isEmpty()) {
            throw new AlreadyCreatedException("Product already created");
        }

        Product product = new Product();
        product.setName(name);
        product.setImage(image);

        em.persist(product);

        return product;
    }
}
