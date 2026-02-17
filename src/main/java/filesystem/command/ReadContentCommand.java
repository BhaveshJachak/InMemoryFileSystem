package filesystem.command;

import filesystem.FileSystem;

/**
 * Command to read content from a file.
 */
public class ReadContentCommand implements Command {
    
    private final FileSystem fileSystem;
    private final String path;
    
    /**
     * Constructor for readContent command.
     * 
     * @param fileSystem The file system instance
     * @param path The path to the file
     */
    public ReadContentCommand(FileSystem fileSystem, String path) {
        this.fileSystem = fileSystem;
        this.path = path;
    }
    
    @Override
    public Object execute() {
        return fileSystem.readContentFromFile(path);
    }
}
