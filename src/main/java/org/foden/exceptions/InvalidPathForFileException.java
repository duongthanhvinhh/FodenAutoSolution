package org.foden.exceptions;

public class InvalidPathForFileException extends FrameworkException{

    /**
     * Instantiates a new Invalid path for file exception.
     *
     * @param message the message
     */
    InvalidPathForFileException(String message){
        super(message);
    }

    /**
     * Instantiates a new Invalid path for file exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    InvalidPathForFileException(String message, Throwable cause){
        super(message,cause);
    }
}
