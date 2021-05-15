package it.polimi.gma.entities;

import javax.persistence.*;

@Table(name = "cancellations")
@Entity
@NamedQuery(name = "Cancellation.deleteByUserAndQuestionnaire",
        query = "DELETE FROM Cancellation c WHERE c.user = :usr AND c.questionnaire = :quest")
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "questionnaire_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
    private Questionnaire questionnaire;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    private User user;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        questionnaire.addCancellation(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
