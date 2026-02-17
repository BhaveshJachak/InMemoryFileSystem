package filesystem.exception;

/**
 * Exception thrown when attempting to perform a directory operation on a file.
 */
public class NotADirectoryException extends FileSystemException {
    
    /**
     * Constructor with path.
     * 
     * @param path The path that is not a directory
     */
    public NotADirectoryException(String path) {
        super("Not a directory: " + path);
    }
}
