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

    /**
     * Returns the product related to the questionnaire of the day
     * @return product of the day
     */
    public Product getProductOfTheDay() {
        try {
            return em.createNamedQuery("Product.getOfTheDay", Product.class)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns the list of all the available products
     * @return list of products
     */
    public List<Product> getAllProducts() {
        return em.createNamedQuery("Product.getAll", Product.class)
                .getResultList();
    }

    /**
     * Returns product with the specified id
     * @param id id of the product
     * @return product
     */
    public Product getProductById(int id) {
        return em.find(Product.class, id);
    }

    /**
     * Create a new product
     * @param name name of the product
     * @param image image of the product
     * @return created product
     * @throws AlreadyCreatedException product with the same name already exists
     */
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
