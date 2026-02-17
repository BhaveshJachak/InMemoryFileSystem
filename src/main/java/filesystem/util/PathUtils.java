package filesystem.util;

import filesystem.exception.InvalidPathException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for path parsing and resolution.
 */
public class PathUtils {
    
    /**
     * Checks if a path is absolute (starts with /).
     * 
     * @param path The path to check
     * @return true if absolute, false otherwise
     */
    public static boolean isAbsolutePath(String path) {
        return path != null && path.startsWith("/");
    }
    
    /**
     * Parses a path string into segments.
     * 
     * @param path The path to parse
     * @return List of path segments
     */
    public static List<String> parsePathToSegments(String path) {
        if (path == null || path.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Remove leading/trailing slashes and split
        String normalized = path.replaceAll("^/+|/+$", "");
        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(normalized.split("/+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * Resolves a target path relative to a current path.
     * Handles . and .. navigation.
     * 
     * @param currentPath The current path segments
     * @param targetPath The target path to resolve
     * @return Resolved path segments
     */
    public static List<String> resolvePath(List<String> currentPath, String targetPath) {
        if (targetPath == null) {
            throw new InvalidPathException("null", "path cannot be null");
        }
        
        List<String> resolved = isAbsolutePath(targetPath) 
                ? new ArrayList<>() 
                : new ArrayList<>(currentPath);
        
        for (String segment : parsePathToSegments(targetPath)) {
            if (segment.equals(".")) {
                continue;
            } else if (segment.equals("..")) {
                if (!resolved.isEmpty()) {
                    resolved.remove(resolved.size() - 1);
                }
                // At root, stay at root (don't throw error)
            } else {
                resolved.add(segment);
            }
        }
        return resolved;
    }
    
    /**
     * Converts path segments to a path string.
     * 
     * @param segments The path segments
     * @return The path string
     */
    public static String segmentsToPath(List<String> segments) {
        if (segments == null || segments.isEmpty()) {
            return "/";
        }
        return "/" + String.join("/", segments);
    }
    
    /**
     * Validates a path segment name.
     * 
     * @param segment The segment to validate
     * @throws InvalidPathException if the segment is invalid
     */
    public static void validateSegment(String segment) {
        if (segment == null || segment.isEmpty()) {
            throw new InvalidPathException(segment, "segment cannot be null or empty");
        }
        if (segment.contains("/")) {
            throw new InvalidPathException(segment, "segment cannot contain /");
        }
    }
}
