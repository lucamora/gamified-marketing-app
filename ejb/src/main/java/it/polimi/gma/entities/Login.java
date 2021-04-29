package it.polimi.gma.entities;

import javax.persistence.*;
import java.util.Date;

@Table(name = "logins")
@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, optional = false)
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}