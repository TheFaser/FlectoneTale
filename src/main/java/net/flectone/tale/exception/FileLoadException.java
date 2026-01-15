package net.flectone.tale.exception;

public class FileLoadException extends RuntimeException {

    public FileLoadException(String file, Throwable cause) {
        super("Failed to read " + file + "\n" + cause.getMessage());
    }

}
