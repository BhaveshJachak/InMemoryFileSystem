package filesystem.node;

/**
 * Represents a file node in the file system.
 * Content is stored as a StringBuilder with synchronized access.
 */
public class FileNode extends FileSystemNode {
    
    private final StringBuilder content;
    
    /**
     * Constructor for file node.
     * 
     * @param name The name of the file
     */
    public FileNode(String name) {
        super(name, NodeType.FILE);
        this.content = new StringBuilder();
    }
    
    /**
     * Appends content to the file.
     * 
     * @param text The text to append
     */
    public synchronized void appendContent(String text) {
        content.append(text);
    }
    
    /**
     * Reads the content of the file.
     * 
     * @return The file content
     */
    public synchronized String readContent() {
        return content.toString();
    }
}
