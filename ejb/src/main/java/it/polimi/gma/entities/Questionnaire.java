package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "questionnaires")
@Entity
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "product_id", nullable = false)
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, optional = false, orphanRemoval = true)
    private Product product;

    @Temporal(TemporalType.DATE)
    private Date date;

    @JoinTable(name = "questionnaires_questions",
            joinColumns = @JoinColumn(name = "questionnaire_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<Question> questions;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}