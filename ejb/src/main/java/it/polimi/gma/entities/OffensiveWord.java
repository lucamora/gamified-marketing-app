package it.polimi.gma.entities;

import javax.persistence.*;

@Table(name = "offensive_words")
@Entity
public class OffensiveWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String word;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}