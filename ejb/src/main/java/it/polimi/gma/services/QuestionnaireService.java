package it.polimi.gma.services;

import it.polimi.gma.entities.Answer;
import it.polimi.gma.entities.Question;
import it.polimi.gma.entities.Questionnaire;
import it.polimi.gma.entities.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Map;

@Stateless(name = "QuestionnaireServiceEJB")
public class QuestionnaireService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

    public QuestionnaireService() {
    }

    public Questionnaire getQuestionnaireOfTheDay() {
        try {
            return em.createNamedQuery("Questionnaire.getOfTheDay", Questionnaire.class)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public boolean submit(Map<Integer, String> answers, User user) {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        for (int id : answers.keySet()) {
            Answer ans = new Answer();

            ans.setQuestion(em.find(Question.class, id));
            ans.setAnswer(answers.get(id));
            ans.setQuestionnaire(questionnaire);
            ans.setUser(user);

            em.persist(ans);
        }

        return true;
    }

    private boolean validate(String answer) {
        return true;
    }
}
