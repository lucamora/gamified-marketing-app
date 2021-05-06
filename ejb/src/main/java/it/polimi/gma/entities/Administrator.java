package it.polimi.gma.entities;

import javax.persistence.*;

@Table(name = "administrators")
@Entity
@NamedQuery(name = "Administrator.checkCredentials",
        query = "SELECT a FROM Administrator a WHERE a.username = :usr AND a.password = :pwd")
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;



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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}