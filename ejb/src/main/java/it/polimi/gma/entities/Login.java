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
    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = false)
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Temporal(TemporalType.TIME)
    private Date time;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public Date getTime() {
        return time;
    }

    public void setDateTime(Date now) {
        this.date = now;
        this.time = now;
    }
}