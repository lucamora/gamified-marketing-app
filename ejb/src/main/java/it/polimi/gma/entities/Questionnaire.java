package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "questionnaires")
@Entity
@NamedQueries({
        @NamedQuery(name = "Questionnaire.getOfTheDay",
                query = "SELECT q FROM Questionnaire q WHERE q.date = CURRENT_DATE"),
        @NamedQuery(name = "Questionnaire.getByDate",
                query = "SELECT q FROM Questionnaire q WHERE q.date = :date"),
        @NamedQuery(name = "Questionnaire.getPast",
                query = "SELECT q FROM Questionnaire q WHERE q.date < CURRENT_DATE"),
        @NamedQuery(name = "Questionnaire.getLeaderboard",
                query = "SELECT DISTINCT a.user FROM Answer a WHERE a.user.blocked = false AND a.questionnaire = :quest ORDER BY a.user.points DESC"),
        @NamedQuery(name = "Questionnaire.getUsersSubmitted",
                query = "SELECT DISTINCT a.user FROM Answer a WHERE a.questionnaire = :quest"),
        @NamedQuery(name = "Questionnaire.getUsersCancelled",
                query = "SELECT DISTINCT c.user FROM Cancellation c WHERE c.questionnaire = :quest")
})
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    private Product product;

    @JoinTable(name = "questionnaires_questions",
            joinColumns = @JoinColumn(name = "questionnaire_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<Question> questions;

    @OrderBy("user, question")
    @OneToMany(mappedBy = "questionnaire", cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "questionnaire", cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Cancellation> cancellations;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        this.questions.add(question);
    }

    public List<Question> filterQuestions(Section section) {
        return questions.stream()
                .filter(q -> q.getSection().equals(section))
                .collect(Collectors.toList());
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<Cancellation> getCancellations() {
        return cancellations;
    }

    public void addCancellation(Cancellation cancellation) {
        this.cancellations.add(cancellation);
    }
}