package org.foden.exceptions;

public class PropertyFileUsageException extends FrameworkException{

    /**
     * Instantiates a new Property file usage exception.
     *
     * @param message the message
     */
    public PropertyFileUsageException(String message){
        super(message);
    }

    /**
     * Instantiates a new Property file usage exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PropertyFileUsageException(String message, Throwable cause){
        super(message,cause);
    }
}
