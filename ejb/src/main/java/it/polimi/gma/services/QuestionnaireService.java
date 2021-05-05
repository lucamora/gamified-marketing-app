package it.polimi.gma.services;

import it.polimi.gma.entities.*;
import it.polimi.gma.exceptions.EmptyAnswerException;
import it.polimi.gma.exceptions.OffensiveWordException;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Stateless(name = "QuestionnaireServiceEJB")
public class QuestionnaireService {
    @PersistenceContext(unitName = "gma_persistence")
    private EntityManager em;

    private List<OffensiveWord> offensiveWords;

    public QuestionnaireService() {
    }

    @PostConstruct
    private void loadOffensiveWords() {
        offensiveWords =
                em.createNamedQuery("OffensiveWord.getAll", OffensiveWord.class)
                        .getResultList();
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

    public void submit(Map<Integer, String> answers, User user) throws OffensiveWordException, EmptyAnswerException {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        for (int id : answers.keySet()) {
            String text = answers.get(id).trim();

            // check if answer contains an offensive word
            if (containsOffensiveWord(text)) {
                throw new OffensiveWordException("Offensive word detected");
            }

            Question question = em.find(Question.class, id);

            // check if mandatory questions have responses
            if (!validateMandatory(question, text)) {
                throw new EmptyAnswerException("Mandatory question is empty");
            }

            // create new answer
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setAnswer(text);
            answer.setQuestionnaire(questionnaire);
            answer.setUser(user);

            em.persist(answer);
        }
    }

    private boolean containsOffensiveWord(String answer) {
        String text = answer.toLowerCase();

        for (OffensiveWord w : offensiveWords) {
            if (text.contains(w.getWord())) {
                return true;
            }
        }

        return false;
    }

    private boolean validateMandatory(Question question, String answer) {
        if (question.getSection().isMandatory()) {
            return !answer.isEmpty();
        }

        return true;
    }
}
