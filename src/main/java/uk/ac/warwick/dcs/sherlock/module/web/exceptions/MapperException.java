package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when the "AddMatch" method in LineMapper is called after
 * the "Fill" method has already ran
 */
public class MapperException extends Exception {
    public MapperException(String errorMessage) {
        super(errorMessage);
    }
}