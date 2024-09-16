package org.foden.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class RetryWorker {
    protected int retries;
    protected Supplier<Object> actionWithoutAssertion;
    protected Supplier<Boolean> actionWithAssertTrue;
    protected Runnable actionAfterFailed;
    protected long interval = 10000;

    protected String messageWhenNull = "Main action return null or false";

    public RetryWorker(){
    }

    public static RetryWorker buildNewRetry(){
        return new RetryWorker();
    }

    public RetryWorker setActionWithoutAssertion(Supplier<Object> act){
        actionWithoutAssertion = act;
        return this;
    }
    public RetryWorker setActionWhenFail(Runnable act){
        actionAfterFailed = act;
        return this;
    }

    public RetryWorker setActionWithAssertTrue(Supplier<Boolean> assertionAct){
        actionWithAssertTrue = assertionAct;
        return this;
    }

    public Object getReturnWithRetries(int retries) {
        Object result = null;
        for (int i = retries; i>0; i--) {
            // actions
            try {
                if (actionWithoutAssertion !=null) {
                    result = actionWithoutAssertion.get();
                    if (result == null)
                        throw new RuntimeException(messageWhenNull);
                    else
                        return result;
                }
                else if (actionWithAssertTrue != null) {
                    result = actionWithAssertTrue.get();
                    if (((Boolean) result) == Boolean.FALSE){
                        result = null;
                        throw new RuntimeException(messageWhenNull);
                    }
                    else
                        return result;
                }
            } catch (Throwable throwable) {
                if (i > 1) {
                    System.out.println("Retry mechanism fails because of: " + throwable);
                    System.out.println("Retry mechanism: performing post action...");
                    if (actionAfterFailed!=null)
                        actionAfterFailed.run();

                    System.out.println("Retry mechanism: Retrying in interval: " + interval);
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                } else {
                    if (throwable.getMessage().equalsIgnoreCase(messageWhenNull)) {
                        System.out.println("Retry mechanism: Action returns null on last try");
                        return null;
                    }
                    System.out.println("Retry mechanism: throw exception on last try, throwing exception: ");
                    throw throwable;
                }
            }
        }
        return null;
    }

    public RetryWorker setInterval(long milliseconds){
        interval = milliseconds;
        return this;
    }

    public Object getReturnWithTimeout(Duration timeout) {
        Instant endTime = Instant.now().plusSeconds(timeout.getSeconds());
        Object result;
        while (true) {
            // actions
            try {
                if (actionWithoutAssertion !=null) {
                    result = actionWithoutAssertion.get();
                    if (result == null)
                        throw new RuntimeException(messageWhenNull);
                    else
                        return result;
                }
                else if (actionWithAssertTrue != null) {
                    result = actionWithAssertTrue.get();
                    if (((Boolean) result) == Boolean.FALSE){
                        result = null;
                        throw new RuntimeException(messageWhenNull);
                    }
                    else
                        return result;
                }
            } catch (Throwable throwable) {
                if (Instant.now().isBefore(endTime)) {
                    System.out.println("Retry mechanism fails because of: " + throwable);
                    System.out.println("Retry mechanism: performing post action...");
                    if (actionAfterFailed!=null)
                        actionAfterFailed.run();
                    System.out.println("Retry mechanism: Retrying in interval: " + interval);
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                } else {
                    if (throwable.getMessage().equalsIgnoreCase(messageWhenNull)) {
                        System.out.println("Retry mechanism: Action returns null on last try");
                        return null;
                    }
                    System.out.println("Retry mechanism: throw exception on last try, throwing exception: ");
                    throw throwable;
                }
            }
        }
    }

    public boolean runWithTimeout(Duration timeout){
        Object result = getReturnWithTimeout(timeout);
        if (result == null)
            return false;
        else
            return true;
    }

    public boolean runWithRetries(int retries) {
        Object result = getReturnWithRetries(retries);
        if (result == null)
            return false;
        else
            return true;
    }
}
