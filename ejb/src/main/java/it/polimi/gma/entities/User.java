package it.polimi.gma.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.checkCredentials",
                query = "SELECT u FROM User u WHERE u.username = :usr AND u.password = :pwd"),
        // TODO: select only users that have filled the questionnaire of the day
        @NamedQuery(name = "User.getLeaderboard",
                query = "SELECT u FROM User u WHERE u.blocked = false ORDER BY u.points DESC")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String email;

    private String password;

    private int points;

    private boolean blocked;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void resetPoints() {
        this.points = 0;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void blockUser() {
        this.blocked = true;
    }
}
