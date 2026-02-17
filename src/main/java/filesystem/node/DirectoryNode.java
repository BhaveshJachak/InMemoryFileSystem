package filesystem.node;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a directory node in the file system.
 * Uses TreeMap with ReadWriteLock for thread-safe, sorted child management.
 */
public class DirectoryNode extends FileSystemNode {
    
    private final TreeMap<String, FileSystemNode> children;
    private final ReadWriteLock lock;
    
    /**
     * Constructor for directory node.
     * 
     * @param name The name of the directory
     */
    public DirectoryNode(String name) {
        super(name, NodeType.DIRECTORY);
        this.children = new TreeMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    /**
     * Lists all children names in sorted order.
     * 
     * @return List of child names
     */
    public List<String> listChildren() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(children.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets a child node by name.
     * 
     * @param name The name of the child
     * @return The child node or null if not found
     */
    public FileSystemNode getChild(String name) {
        lock.readLock().lock();
        try {
            return children.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Adds a child if absent using optimistic locking pattern.
     * 
     * @param name The name of the child
     * @param creator Function to create the child if absent
     * @return The existing or newly created child node
     */
    public FileSystemNode addChildIfAbsent(String name, 
            Function<String, FileSystemNode> creator) {
        // Optimistic read first
        lock.readLock().lock();
        try {
            FileSystemNode existing = children.get(name);
            if (existing != null) return existing;
        } finally {
            lock.readLock().unlock();
        }
        
        // Acquire write lock
        lock.writeLock().lock();
        try {
            // Double-check
            FileSystemNode existing = children.get(name);
            if (existing != null) return existing;
            
            FileSystemNode newNode = creator.apply(name);
            children.put(name, newNode);
            return newNode;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Computes a new value for a child using a remapping function.
     * 
     * @param name The name of the child
     * @param remapper Function to compute the new value
     * @return The new value for the child
     */
    public FileSystemNode compute(String name,
            BiFunction<String, FileSystemNode, FileSystemNode> remapper) {
        lock.writeLock().lock();
        try {
            FileSystemNode existing = children.get(name);
            FileSystemNode newValue = remapper.apply(name, existing);
            if (newValue == null) {
                children.remove(name);
            } else {
                children.put(name, newValue);
            }
            return newValue;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
