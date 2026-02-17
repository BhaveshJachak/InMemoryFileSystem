package filesystem.node;

/**
 * Abstract base class for file system nodes (Composite Pattern).
 * Represents both files and directories in the file system.
 */
public abstract class FileSystemNode {
    
    protected final String name;
    protected final NodeType type;
    
    /**
     * Constructor for file system node.
     * 
     * @param name The name of the node
     * @param type The type of the node (FILE or DIRECTORY)
     */
    public FileSystemNode(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * Gets the name of the node.
     * 
     * @return The node name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the type of the node.
     * 
     * @return The node type
     */
    public NodeType getType() {
        return type;
    }
    
    /**
     * Checks if this node is a directory.
     * 
     * @return true if directory, false otherwise
     */
    public boolean isDirectory() {
        return type == NodeType.DIRECTORY;
    }
    
    /**
     * Checks if this node is a file.
     * 
     * @return true if file, false otherwise
     */
    public boolean isFile() {
        return type == NodeType.FILE;
    }
}
