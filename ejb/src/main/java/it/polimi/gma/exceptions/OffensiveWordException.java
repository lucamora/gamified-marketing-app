package it.polimi.gma.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class OffensiveWordException extends Exception {
    public OffensiveWordException(String message) {
        super(message);
    }
}
