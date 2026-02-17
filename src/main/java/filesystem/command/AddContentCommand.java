package filesystem.command;

import filesystem.FileSystem;

/**
 * Command to add content to a file.
 */
public class AddContentCommand implements Command {
    
    private final FileSystem fileSystem;
    private final String path;
    private final String content;
    
    /**
     * Constructor for addContent command.
     * 
     * @param fileSystem The file system instance
     * @param path The path to the file
     * @param content The content to add
     */
    public AddContentCommand(FileSystem fileSystem, String path, String content) {
        this.fileSystem = fileSystem;
        this.path = path;
        this.content = content;
    }
    
    @Override
    public Object execute() {
        fileSystem.addContentToFile(path, content);
        return null;
    }
}
