package filesystem.command;

import filesystem.FileSystem;

/**
 * Command to print working directory.
 */
public class PwdCommand implements Command {
    
    private final FileSystem fileSystem;
    
    /**
     * Constructor for pwd command.
     * 
     * @param fileSystem The file system instance
     */
    public PwdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public Object execute() {
        return fileSystem.pwd();
    }
}
