package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "questionnaires")
@Entity
@NamedQuery(name = "Questionnaire.getOfTheDay",
        query = "SELECT q FROM Questionnaire q WHERE q.product.date = CURRENT_DATE")
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "product_id", nullable = false)
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, optional = false, orphanRemoval = true)
    private Product product;

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

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Question> filterQuestions(Section section) {
        return questions.stream()
                .filter(q -> q.getSection().equals(section))
                .collect(Collectors.toList());
    }
}