package filesystem.factory;

import filesystem.FileSystem;
import filesystem.command.*;

/**
 * Factory for creating file system commands (Factory Pattern).
 */
public class CommandFactory {
    
    private final FileSystem fileSystem;
    
    /**
     * Constructor for command factory.
     * 
     * @param fileSystem The file system instance
     */
    public CommandFactory(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Creates a mkdir command.
     * 
     * @param path The path to create
     * @return MkdirCommand instance
     */
    public Command createMkdirCommand(String path) {
        return new MkdirCommand(fileSystem, path);
    }
    
    /**
     * Creates an ls command.
     * 
     * @param path The path to list
     * @return LsCommand instance
     */
    public Command createLsCommand(String path) {
        return new LsCommand(fileSystem, path);
    }
    
    /**
     * Creates a cd command.
     * 
     * @param path The path to change to
     * @return CdCommand instance
     */
    public Command createCdCommand(String path) {
        return new CdCommand(fileSystem, path);
    }
    
    /**
     * Creates an addContent command.
     * 
     * @param path The path to the file
     * @param content The content to add
     * @return AddContentCommand instance
     */
    public Command createAddContentCommand(String path, String content) {
        return new AddContentCommand(fileSystem, path, content);
    }
    
    /**
     * Creates a readContent command.
     * 
     * @param path The path to the file
     * @return ReadContentCommand instance
     */
    public Command createReadContentCommand(String path) {
        return new ReadContentCommand(fileSystem, path);
    }
    
    /**
     * Creates a pwd command.
     * 
     * @return PwdCommand instance
     */
    public Command createPwdCommand() {
        return new PwdCommand(fileSystem);
    }
}
