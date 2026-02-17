package filesystem.command;

import filesystem.FileSystem;

/**
 * Command to change current directory.
 */
public class CdCommand implements Command {
    
    private final FileSystem fileSystem;
    private final String path;
    
    /**
     * Constructor for cd command.
     * 
     * @param fileSystem The file system instance
     * @param path The path to change to
     */
    public CdCommand(FileSystem fileSystem, String path) {
        this.fileSystem = fileSystem;
        this.path = path;
    }
    
    @Override
    public Object execute() {
        fileSystem.cd(path);
        return null;
    }
}
