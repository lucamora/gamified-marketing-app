package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "products")
@Entity
@NamedQuery(name = "Product.getOfTheDay",
        query = "SELECT p FROM Product p WHERE p.date = CURRENT_DATE")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String image;

    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToOne(mappedBy = "product", cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private Questionnaire questionnaire;

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
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}