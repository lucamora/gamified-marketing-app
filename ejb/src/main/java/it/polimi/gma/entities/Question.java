package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.List;

@Table(name = "questions")
@Entity
@NamedQuery(name = "Question.getStatistical",
        query = "SELECT q FROM Question q WHERE q.section = it.polimi.gma.entities.Section.STATISTICAL")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String question;

    @Enumerated(EnumType.STRING)
    private Section section;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Choice> choices;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}