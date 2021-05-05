package it.polimi.gma.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class EmptyAnswerException extends Exception {
    public EmptyAnswerException(String message) {
        super(message);
    }
}
