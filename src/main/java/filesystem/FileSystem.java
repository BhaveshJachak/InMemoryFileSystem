package filesystem;

import filesystem.exception.*;
import filesystem.node.*;
import filesystem.util.PathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main file system class implementing Singleton and Facade patterns.
 * Provides thread-safe in-memory file system operations.
 */
public class FileSystem {
    
    private static volatile FileSystem instance;
    
    private final DirectoryNode root;
    private final ReadWriteLock stateLock;
    private List<String> currentPathSegments;
    private DirectoryNode currentDirectory;
    
    /**
     * Private constructor for singleton pattern.
     */
    private FileSystem() {
        this.root = new DirectoryNode("");
        this.stateLock = new ReentrantReadWriteLock();
        this.currentPathSegments = new ArrayList<>();
        this.currentDirectory = root;
    }
    
    /**
     * Gets the singleton instance of the file system.
     * 
     * @return The file system instance
     */
    public static FileSystem getInstance() {
        if (instance == null) {
            synchronized (FileSystem.class) {
                if (instance == null) {
                    instance = new FileSystem();
                }
            }
        }
        return instance;
    }
    
    /**
     * Creates a directory at the specified path.
     * Auto-creates parent directories if needed (like mkdir -p).
     * 
     * @param path The path to create
     */
    public void mkdir(String path) {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException(path, "path cannot be null or empty");
        }
        
        stateLock.readLock().lock();
        List<String> segments;
        try {
            segments = PathUtils.resolvePath(
                    new ArrayList<>(currentPathSegments), path);
        } finally {
            stateLock.readLock().unlock();
        }
        
        DirectoryNode current = root;
        for (String segment : segments) {
            PathUtils.validateSegment(segment);
            FileSystemNode node = current.addChildIfAbsent(segment, DirectoryNode::new);
            
            if (!node.isDirectory()) {
                throw new NotADirectoryException(PathUtils.segmentsToPath(segments));
            }
            current = (DirectoryNode) node;
        }
    }
    
    /**
     * Lists the contents of a directory or returns the file name if path is a file.
     * 
     * @param path The path to list
     * @return List of names (sorted for directories)
     */
    public List<String> ls(String path) {
        stateLock.readLock().lock();
        List<String> segments;
        try {
            if (path == null || path.isEmpty()) {
                // List current directory
                return currentDirectory.listChildren();
            }
            segments = PathUtils.resolvePath(
                    new ArrayList<>(currentPathSegments), path);
        } finally {
            stateLock.readLock().unlock();
        }
        
        FileSystemNode node = traverseToNode(segments);
        
        if (node == null) {
            throw new PathNotFoundException(path);
        }
        
        if (node.isFile()) {
            // Return just the file name
            return Collections.singletonList(node.getName());
        }
        
        DirectoryNode dir = (DirectoryNode) node;
        return dir.listChildren();
    }
    
    /**
     * Changes the current working directory.
     * 
     * @param path The path to change to
     */
    public void cd(String path) {
        cdInternal(path);
    }
    
    /**
     * Internal method for changing directory with proper locking.
     * 
     * @param path The path to change to
     */
    private void cdInternal(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            stateLock.writeLock().lock();
            try {
                currentPathSegments = new ArrayList<>();
                currentDirectory = root;
                return;
            } finally {
                stateLock.writeLock().unlock();
            }
        }
        
        // WRITE LOCK for entire cd - ensures atomicity
        stateLock.writeLock().lock();
        try {
            List<String> segments = PathUtils.resolvePath(
                    new ArrayList<>(currentPathSegments), path);
            
            FileSystemNode node = traverseToNode(segments);
            
            if (node == null) {
                throw new PathNotFoundException(path);
            }
            if (!node.isDirectory()) {
                throw new NotADirectoryException(path);
            }
            
            // Update atomically
            currentPathSegments = segments;
            currentDirectory = (DirectoryNode) node;
        } finally {
            stateLock.writeLock().unlock();
        }
    }
    
    /**
     * Adds content to a file, creating it if it doesn't exist.
     * 
     * @param path The path to the file
     * @param content The content to add
     */
    public void addContentToFile(String path, String content) {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException(path, "path cannot be null or empty");
        }
        
        stateLock.readLock().lock();
        List<String> segments;
        try {
            segments = PathUtils.resolvePath(
                    new ArrayList<>(currentPathSegments), path);
        } finally {
            stateLock.readLock().unlock();
        }
        
        if (segments.isEmpty()) {
            throw new InvalidPathException(path, "cannot create file at root");
        }
        
        // Navigate to parent directory
        List<String> parentSegments = segments.subList(0, segments.size() - 1);
        DirectoryNode parent = (DirectoryNode) traverseToNode(parentSegments);
        
        if (parent == null) {
            // Create parent directories
            mkdir(PathUtils.segmentsToPath(parentSegments));
            parent = (DirectoryNode) traverseToNode(parentSegments);
        }
        
        String fileName = segments.get(segments.size() - 1);
        PathUtils.validateSegment(fileName);
        
        // Get or create file
        FileSystemNode node = parent.compute(fileName, (name, existing) -> {
            if (existing != null) {
                if (!existing.isFile()) {
                    throw new NotAFileException(path);
                }
                return existing;
            }
            return new FileNode(name);
        });
        
        FileNode file = (FileNode) node;
        file.appendContent(content);
    }
    
    /**
     * Reads content from a file.
     * 
     * @param path The path to the file
     * @return The file content
     */
    public String readContentFromFile(String path) {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException(path, "path cannot be null or empty");
        }
        
        stateLock.readLock().lock();
        List<String> segments;
        try {
            segments = PathUtils.resolvePath(
                    new ArrayList<>(currentPathSegments), path);
        } finally {
            stateLock.readLock().unlock();
        }
        
        FileSystemNode node = traverseToNode(segments);
        
        if (node == null) {
            throw new PathNotFoundException(path);
        }
        
        if (!node.isFile()) {
            throw new NotAFileException(path);
        }
        
        FileNode file = (FileNode) node;
        return file.readContent();
    }
    
    /**
     * Returns the current working directory path.
     * 
     * @return The current working directory path
     */
    public String pwd() {
        stateLock.readLock().lock();
        try {
            return PathUtils.segmentsToPath(currentPathSegments);
        } finally {
            stateLock.readLock().unlock();
        }
    }
    
    /**
     * Traverses to a node given path segments.
     * 
     * @param segments The path segments
     * @return The node at the path, or null if not found
     */
    private FileSystemNode traverseToNode(List<String> segments) {
        FileSystemNode current = root;
        
        for (String segment : segments) {
            if (current == null || !current.isDirectory()) {
                return null;
            }
            
            DirectoryNode dir = (DirectoryNode) current;
            current = dir.getChild(segment);
            
            if (current == null) {
                return null;
            }
        }
        
        return current;
    }
    
    /**
     * Resets the file system to its initial state (useful for testing).
     */
    public void reset() {
        stateLock.writeLock().lock();
        try {
            // Clear the root directory
            DirectoryNode newRoot = new DirectoryNode("");
            // We can't replace root, so we clear it by creating new children map
            // However, since children is final, we use a different approach
            currentPathSegments = new ArrayList<>();
            currentDirectory = root;
            
            // Clear all children from root
            for (String child : root.listChildren()) {
                root.compute(child, (name, node) -> null);
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }
}
