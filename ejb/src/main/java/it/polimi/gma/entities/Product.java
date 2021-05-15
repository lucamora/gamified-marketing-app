package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.Base64;
import java.util.List;

@Table(name = "products")
@Entity
@NamedQueries({
        @NamedQuery(name = "Product.getOfTheDay",
                query = "SELECT q.product FROM Questionnaire q WHERE q.date = CURRENT_DATE"),
        @NamedQuery(name = "Product.getByName",
                query = "SELECT p FROM Product p WHERE p.name = :name"),
        @NamedQuery(name = "Product.getAll",
                query = "SELECT p FROM Product p")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private byte[] image;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Review> reviews;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Base64.getMimeEncoder().encodeToString(image);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}