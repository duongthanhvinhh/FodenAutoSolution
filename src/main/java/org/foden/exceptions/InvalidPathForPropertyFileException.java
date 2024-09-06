package org.foden.exceptions;

public class InvalidPathForPropertyFileException extends InvalidPathForFileException{

    /**
     * Instantiates a new Invalid path for property file exception.
     *
     * @param message the message
     */
    public InvalidPathForPropertyFileException(String message){
        super(message);
    }

    /**
     * Instantiates a new Invalid path for property file exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InvalidPathForPropertyFileException(String message, Throwable cause){
        super(message,cause);
    }
}
