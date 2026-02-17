package filesystem.exception;

/**
 * Exception thrown when a path is not found in the file system.
 */
public class PathNotFoundException extends FileSystemException {
    
    /**
     * Constructor with path.
     * 
     * @param path The path that was not found
     */
    public PathNotFoundException(String path) {
        super("Path not found: " + path);
    }
}
