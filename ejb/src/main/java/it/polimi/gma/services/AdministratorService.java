package it.polimi.gma.services;

import it.polimi.gma.entities.Administrator;
import it.polimi.gma.exceptions.CredentialsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless(name = "AdministratorServiceEJB")
public class AdministratorService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

    public AdministratorService() {
    }

    public Administrator checkCredentials(String username, String password) throws CredentialsException {
        List<Administrator> admins = null;
        try {
            admins = em.createNamedQuery("Administrator.checkCredentials", Administrator.class)
                    .setParameter("usr", username)
                    .setParameter("pwd", password)
                    .getResultList();
        }
        catch (PersistenceException e) {
            throw new CredentialsException("Could not verify credentials");
        }

        if (admins.size() != 1) {
            return null;
        }

        return admins.get(0);
    }
}
