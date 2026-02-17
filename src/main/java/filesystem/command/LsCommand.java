package filesystem.command;

import filesystem.FileSystem;
import java.util.List;

/**
 * Command to list directory contents or return file name.
 */
public class LsCommand implements Command {
    
    private final FileSystem fileSystem;
    private final String path;
    
    /**
     * Constructor for ls command.
     * 
     * @param fileSystem The file system instance
     * @param path The path to list
     */
    public LsCommand(FileSystem fileSystem, String path) {
        this.fileSystem = fileSystem;
        this.path = path;
    }
    
    @Override
    public Object execute() {
        return fileSystem.ls(path);
    }
}
