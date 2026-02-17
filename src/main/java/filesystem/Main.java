package filesystem;

import filesystem.factory.CommandFactory;
import filesystem.command.Command;
import filesystem.exception.FileSystemException;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive command-line interface for the file system.
 */
public class Main {
    
    public static void main(String[] args) {
        FileSystem fs = FileSystem.getInstance();
        CommandFactory factory = new CommandFactory(fs);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===================================");
        System.out.println("  In-Memory File System");
        System.out.println("===================================");
        System.out.println("Commands:");
        System.out.println("  mkdir <path>           - Create directory");
        System.out.println("  ls [path]              - List directory contents");
        System.out.println("  cd <path>              - Change directory");
        System.out.println("  pwd                    - Print working directory");
        System.out.println("  touch <path> <content> - Add content to file");
        System.out.println("  cat <path>             - Read file content");
        System.out.println("  exit                   - Exit the program");
        System.out.println("===================================\n");
        
        while (true) {
            try {
                System.out.print(fs.pwd() + " $ ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                String[] parts = input.split("\\s+", 3);
                String cmd = parts[0].toLowerCase();
                
                if (cmd.equals("exit") || cmd.equals("quit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                
                Command command = null;
                Object result = null;
                
                switch (cmd) {
                    case "mkdir":
                        if (parts.length < 2) {
                            System.err.println("Usage: mkdir <path>");
                            continue;
                        }
                        command = factory.createMkdirCommand(parts[1]);
                        break;
                        
                    case "ls":
                        String lsPath = parts.length >= 2 ? parts[1] : "";
                        command = factory.createLsCommand(lsPath);
                        break;
                        
                    case "cd":
                        if (parts.length < 2) {
                            System.err.println("Usage: cd <path>");
                            continue;
                        }
                        command = factory.createCdCommand(parts[1]);
                        break;
                        
                    case "pwd":
                        command = factory.createPwdCommand();
                        break;
                        
                    case "touch":
                        if (parts.length < 3) {
                            System.err.println("Usage: touch <path> <content>");
                            continue;
                        }
                        command = factory.createAddContentCommand(parts[1], parts[2]);
                        break;
                        
                    case "cat":
                        if (parts.length < 2) {
                            System.err.println("Usage: cat <path>");
                            continue;
                        }
                        command = factory.createReadContentCommand(parts[1]);
                        break;
                        
                    default:
                        System.err.println("Unknown command: " + cmd);
                        continue;
                }
                
                if (command != null) {
                    result = command.execute();
                    
                    if (result != null) {
                        if (result instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<String> items = (List<String>) result;
                            for (String item : items) {
                                System.out.println(item);
                            }
                        } else {
                            System.out.println(result);
                        }
                    }
                }
                
            } catch (FileSystemException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
    }
}
