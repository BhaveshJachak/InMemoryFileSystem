package filesystem.command;

import filesystem.FileSystem;

/**
 * Command to create directories.
 */
public class MkdirCommand implements Command {
    
    private final FileSystem fileSystem;
    private final String path;
    
    /**
     * Constructor for mkdir command.
     * 
     * @param fileSystem The file system instance
     * @param path The path to create
     */
    public MkdirCommand(FileSystem fileSystem, String path) {
        this.fileSystem = fileSystem;
        this.path = path;
    }
    
    @Override
    public Object execute() {
        fileSystem.mkdir(path);
        return null;
    }
}
