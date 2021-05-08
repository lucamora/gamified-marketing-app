package it.polimi.gma.services;

import it.polimi.gma.entities.*;
import it.polimi.gma.exceptions.EmptyAnswerException;
import it.polimi.gma.exceptions.InexistentQuestionnaire;
import it.polimi.gma.exceptions.InvalidDateException;
import it.polimi.gma.exceptions.OffensiveWordException;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZoneId;
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

    /**
     * Returns the questionnaire of the day
     * @return questionnaire of the day
     */
    public Questionnaire getQuestionnaireOfTheDay() {
        try {
            return em.createNamedQuery("Questionnaire.getOfTheDay", Questionnaire.class)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Create a new questionnaire and associate it to a product
     * @param product product related to the questionnaire
     * @param questions marketing questions of the questionnaire
     */
    public void createQuestionnaire(Product product, List<String> questions) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setProduct(product);

        for (String text : questions) {
            Question question = new Question();
            question.setQuestion(text.trim());
            question.setSection(Section.MARKETING);

            questionnaire.addQuestion(question);
        }

        // add statically statistical questions
        //questionnaire.addQuestion(em.find(Question.class, 1));
        //questionnaire.addQuestion(em.find(Question.class, 2));
        //questionnaire.addQuestion(em.find(Question.class, 3));

        em.persist(questionnaire);
    }

    /**
     * Return the questionnaires proposed in the past
     * @return past questionnaires
     */
    public List<Questionnaire> getPastQuestionnaire() {
        return em.createNamedQuery("Questionnaire.getPast", Questionnaire.class)
                .getResultList();
    }

    /**
     * Delete a questionnaire
     * @param id id of the questionnaire
     */
    public Questionnaire deleteQuestionnaire(int id) throws InvalidDateException, InexistentQuestionnaire {
        Questionnaire questionnaire = em.find(Questionnaire.class, id);

        if (questionnaire == null) {
            throw new InexistentQuestionnaire("Questionnaire does not exist");
        }

        // check if the date is valid (date preceding the current date)
        LocalDate date = questionnaire.getProduct().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!date.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Date should be a past date");
        }

        em.remove(questionnaire);
        return questionnaire;
    }

    /**
     * Check if a user has already submitted the questionnaire of the day
     * @param user user to be checked
     * @return true if the user has not already submitted the questionnaire
     */
    public boolean checkIfCanSubmit(User user) {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        List<Answer> answers = em.createNamedQuery("Answer.getByUserAndQuestionnaire", Answer.class)
                .setParameter("usr", user)
                .setParameter("quest", questionnaire)
                .getResultList();

        return answers.isEmpty();
    }

    /**
     * Validate and save a submitted questionnaire
     * @param answers answers of the user
     * @param user user that submitted the questionnaire
     * @throws OffensiveWordException user inserted an offensive word
     * @throws EmptyAnswerException user has not compiled a mandatory question
     */
    public void submit(Map<Integer, String> answers, User user) throws OffensiveWordException, EmptyAnswerException {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        for (int id : answers.keySet()) {
            String text = answers.get(id).trim();

            // check if answer contains an offensive word
            if (containsOffensiveWord(text)) {
                throw new OffensiveWordException("Offensive word detected");
            }

            Question question = em.find(Question.class, id);

            // check if mandatory question has an answer
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

    /**
     * Check if an answer contains an offensive word
     * @param answer answer to be checked
     * @return true if answer contains an offensive word
     */
    private boolean containsOffensiveWord(String answer) {
        String text = answer.toLowerCase();

        for (OffensiveWord w : offensiveWords) {
            if (text.contains(w.getWord())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check that mandatory questions are not empty
     * @param question question to be checked
     * @param answer answer of the user
     * @return false if a mandatory question has an empty answer
     */
    private boolean validateMandatory(Question question, String answer) {
        if (question.getSection().isMandatory()) {
            return !answer.isEmpty();
        }

        return true;
    }
}
