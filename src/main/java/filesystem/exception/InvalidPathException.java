package filesystem.exception;

/**
 * Exception thrown when a path is invalid.
 */
public class InvalidPathException extends FileSystemException {
    
    /**
     * Constructor with path and reason.
     * 
     * @param path The invalid path
     * @param reason The reason why the path is invalid
     */
    public InvalidPathException(String path, String reason) {
        super("Invalid path '" + path + "': " + reason);
    }
}
