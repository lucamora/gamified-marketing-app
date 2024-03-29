package it.polimi.gma.entities;

import javax.persistence.*;

@Table(name = "answers")
@Entity
@NamedQuery(name = "Answer.getByUserAndQuestionnaire",
        query = "SELECT a FROM Answer a WHERE a.user = :usr AND a.questionnaire = :quest")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String answer;

    @JoinColumn(name = "questionnaire_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Questionnaire questionnaire;

    @JoinColumn(name = "question_id", nullable = false)
    @ManyToOne(optional = false)
    private Question question;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private User user;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        questionnaire.addAnswer(this);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}