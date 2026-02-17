package filesystem.exception;

/**
 * Exception thrown when attempting to perform a file operation on a directory.
 */
public class NotAFileException extends FileSystemException {
    
    /**
     * Constructor with path.
     * 
     * @param path The path that is not a file
     */
    public NotAFileException(String path) {
        super("Not a file: " + path);
    }
}
