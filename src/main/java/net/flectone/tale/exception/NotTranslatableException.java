package net.flectone.tale.exception;

public class NotTranslatableException extends RuntimeException {

    public NotTranslatableException() {
        super("Component is not translatable or has an unknown translation key");
    }

}
