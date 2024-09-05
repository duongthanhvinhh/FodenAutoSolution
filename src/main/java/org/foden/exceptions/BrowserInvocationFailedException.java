package org.foden.exceptions;

public class BrowserInvocationFailedException extends FrameworkException{

    public BrowserInvocationFailedException(String message){
        super(message);
    }

    public BrowserInvocationFailedException(String message, Throwable cause){
        super(message, cause);
    }
}
