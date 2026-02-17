package filesystem.exception;

/**
 * Base exception class for file system operations.
 */
public class FileSystemException extends RuntimeException {
    
    /**
     * Constructor with message.
     * 
     * @param message The exception message
     */
    public FileSystemException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause.
     * 
     * @param message The exception message
     * @param cause The cause of the exception
     */
    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
