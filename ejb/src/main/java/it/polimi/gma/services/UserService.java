package it.polimi.gma.services;

import it.polimi.gma.entities.Login;
import it.polimi.gma.entities.User;
import it.polimi.gma.exceptions.InvalidCredentialsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

@Stateless(name = "UserServiceEJB")
public class UserService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

    public UserService() {
    }

    public User checkCredentials(String username, String password) throws InvalidCredentialsException {
        List<User> users = null;
        try {
            users = em.createNamedQuery("User.checkCredentials", User.class)
                    .setParameter("usr", username)
                    .setParameter("pwd", password)
                    .getResultList();
        }
        catch (PersistenceException e) {
            throw new InvalidCredentialsException("Could not verify credentials");
        }

        if (users.size() != 1) {
            return null;
        }

        return users.get(0);
    }

    public void saveLogin(User user) {
        Login login = new Login();
        login.setUser(user);
        login.setDate(new Date());

        em.persist(login);
    }

    public void blockUser(User user) {
        user.blockUser();
        em.merge(user);
    }

    public void resetPoints(User user) {
        user.resetPoints();
        em.merge(user);
    }
}
