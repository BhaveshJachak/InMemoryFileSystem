package filesystem.command;

/**
 * Command interface for file system operations (Command Pattern).
 */
public interface Command {
    
    /**
     * Executes the command.
     * 
     * @return The result of the command execution
     */
    Object execute();
}
