package it.polimi.gma.services;

import it.polimi.gma.entities.*;
import it.polimi.gma.exceptions.*;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
     * Returns questionnaire with the specified id
     * @param id id of the questionnaire
     * @return questionnaire
     */
    public Questionnaire getQuestionnaireById(int id) {
        return em.find(Questionnaire.class, id);
    }

    /**
     * Create a new questionnaire and associate it to a product
     * @param product product related to the questionnaire
     * @param date date of the questionnaire
     * @param questions marketing questions of the questionnaire
     * @return created questionnaire
     * @throws InvalidDateException specified date is in the past
     * @throws AlreadyCreatedException questionnaire in the same date already exists
     */
    public Questionnaire createQuestionnaire(Product product, Date date, List<String> questions) throws InvalidDateException, AlreadyCreatedException {
        // check if the date is valid (current date or a future date)
        LocalDate publish = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (publish.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Date should be today or a future date");
        }

        List<Questionnaire> questionnaires =
                em.createNamedQuery("Questionnaire.getByDate", Questionnaire.class)
                        .setParameter("date", date)
                        .getResultList();

        // check if a questionnaire for the requested date already exist
        if (!questionnaires.isEmpty()) {
            throw new AlreadyCreatedException("Questionnaire for the day already created");
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setProduct(product);
        questionnaire.setDate(date);

        for (String text : questions) {
            Question question = new Question();
            question.setQuestion(text);
            question.setSection(Section.MARKETING);

            questionnaire.addQuestion(question);
        }

        em.persist(questionnaire);

        return questionnaire;
    }

    /**
     * Return the questionnaires proposed in the past
     * @return past questionnaires
     */
    public List<Questionnaire> getPastQuestionnaires() {
        return em.createNamedQuery("Questionnaire.getPast", Questionnaire.class)
                .getResultList();
    }

    /**
     * Delete a questionnaire with the specified id
     * @param id id of the questionnaire
     * @return deleted questionnaire
     * @throws InvalidDateException specified date is today or a future date
     * @throws InexistentQuestionnaire questionnaire does not exists
     */
    public Questionnaire deleteQuestionnaire(int id) throws InvalidDateException, InexistentQuestionnaire {
        Questionnaire questionnaire = em.find(Questionnaire.class, id);

        if (questionnaire == null) {
            throw new InexistentQuestionnaire("Questionnaire does not exist");
        }

        // check if the date is valid (date preceding the current date)
        LocalDate date = questionnaire.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!date.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Date should be a past date");
        }

        em.remove(questionnaire);
        return questionnaire;
    }

    /**
     * Check if a user can submit the questionnaire of the day (has not already submitted)
     * @param user user to be checked
     * @return true if the user can submit (has not already submitted the questionnaire)
     */
    public boolean checkUserCanSubmit(User user) {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        // if there is no questionnaire of the day
        // obviously the user can not submit/cancel it
        if (questionnaire == null) {
            return false;
        }

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

        // delete previous cancellation performed by the user
        deleteCancellations(questionnaire, user);

        for (int id : answers.keySet()) {
            String text = answers.get(id);

            // check if answer contains an offensive word
            if (containsOffensiveWord(text)) {
                throw new OffensiveWordException("Offensive word detected");
            }

            Question question = em.find(Question.class, id);

            // check if mandatory question has an answer
            if (!validateMandatory(question, text)) {
                throw new EmptyAnswerException("Mandatory question is empty");
            }

            // store only filled in answers
            if (!text.isEmpty()) {
                // create new answer
                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setAnswer(text);
                answer.setQuestionnaire(questionnaire);
                answer.setUser(user);

                em.persist(answer);
            }
        }
    }

    /**
     * Save the cancellation of the questionnaire of the day by a user
     * @param user user that cancelled the questionnaire
     */
    public void cancel(User user) {
        Questionnaire questionnaire = getQuestionnaireOfTheDay();

        Cancellation cancellation = new Cancellation();
        cancellation.setQuestionnaire(questionnaire);
        cancellation.setUser(user);

        em.persist(cancellation);
    }

    /**
     * Return the leaderboard for the questionnaire of the day
     * @return leaderboard of the questionnaire of the day
     */
    public List<User> getLeaderboard() {
        return em.createNamedQuery("Questionnaire.getLeaderboard", User.class)
                .setParameter("quest", getQuestionnaireOfTheDay())
                .getResultList();
    }

    /**
     * Return the users that submitted the specified questionnaire
     * @param questionnaire questionnaire of interest
     * @return users that submitted the questionnaire
     */
    public List<User> getUsersSubmitted(Questionnaire questionnaire) {
        return em.createNamedQuery("Questionnaire.getUsersSubmitted", User.class)
                .setParameter("quest", questionnaire)
                .getResultList();
    }

    /**
     * Return the users that cancelled the specified questionnaire
     * @param questionnaire questionnaire of interest
     * @return users that cancelled the questionnaire
     */
    public List<User> getUsersCancelled(Questionnaire questionnaire) {
        return em.createNamedQuery("Questionnaire.getUsersCancelled", User.class)
                .setParameter("quest", questionnaire)
                .getResultList();
    }

    /**
     * Return the list of statistical questions
     * @return statistical questions
     */
    public List<Question> getStatisticalQuestions() {
        return em.createNamedQuery("Question.getStatistical", Question.class)
                .getResultList();
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

    /**
     * Remove the cancellation records performed by a user
     * @param questionnaire cancelled questionnaire
     * @param user user that performed the cancellation
     */
    private void deleteCancellations(Questionnaire questionnaire, User user) {
        em.createNamedQuery("Cancellation.deleteByUserAndQuestionnaire", Cancellation.class)
                .setParameter("usr", user)
                .setParameter("quest", questionnaire)
                .executeUpdate();
    }
}
