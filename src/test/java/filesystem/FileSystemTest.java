package filesystem;

import filesystem.exception.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the file system.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileSystemTest {
    
    private FileSystem fs;
    
    @BeforeEach
    void setUp() {
        fs = FileSystem.getInstance();
        fs.reset();
    }
    
    // ===== MKDIR TESTS =====
    
    @Test
    @Order(1)
    @DisplayName("mkdir: Create single directory")
    void testMkdirSingleDirectory() {
        fs.mkdir("/a");
        List<String> result = fs.ls("/");
        assertEquals(Arrays.asList("a"), result);
    }
    
    @Test
    @Order(2)
    @DisplayName("mkdir: Create nested directories")
    void testMkdirNestedDirectories() {
        fs.mkdir("/a/b/c");
        List<String> result = fs.ls("/a/b");
        assertEquals(Arrays.asList("c"), result);
    }
    
    @Test
    @Order(3)
    @DisplayName("mkdir: Create siblings - verify sorted output")
    void testMkdirSiblings() {
        fs.mkdir("/z");
        fs.mkdir("/a");
        fs.mkdir("/m");
        List<String> result = fs.ls("/");
        assertEquals(Arrays.asList("a", "m", "z"), result);
    }
    
    @Test
    @Order(4)
    @DisplayName("mkdir: Idempotent - no error on existing")
    void testMkdirIdempotent() {
        fs.mkdir("/a/b");
        assertDoesNotThrow(() -> fs.mkdir("/a/b"));
        assertDoesNotThrow(() -> fs.mkdir("/a"));
    }
    
    // ===== LS TESTS =====
    
    @Test
    @Order(5)
    @DisplayName("ls: List root directory")
    void testLsRoot() {
        fs.mkdir("/a");
        fs.mkdir("/b");
        List<String> result = fs.ls("/");
        assertEquals(Arrays.asList("a", "b"), result);
    }
    
    @Test
    @Order(6)
    @DisplayName("ls: List empty directory")
    void testLsEmptyDirectory() {
        fs.mkdir("/empty");
        List<String> result = fs.ls("/empty");
        assertTrue(result.isEmpty());
    }
    
    @Test
    @Order(7)
    @DisplayName("ls: List file returns filename")
    void testLsFile() {
        fs.addContentToFile("/file.txt", "content");
        List<String> result = fs.ls("/file.txt");
        assertEquals(Arrays.asList("file.txt"), result);
    }
    
    @Test
    @Order(8)
    @DisplayName("ls: List current directory")
    void testLsCurrentDirectory() {
        fs.mkdir("/a/b");
        fs.cd("/a");
        fs.mkdir("c");
        List<String> result = fs.ls("");
        assertEquals(Arrays.asList("b", "c"), result);
    }
    
    @Test
    @Order(9)
    @DisplayName("ls: Mixed content sorted alphabetically")
    void testLsMixedContent() {
        fs.mkdir("/dir1");
        fs.addContentToFile("/file1.txt", "content");
        fs.mkdir("/dir2");
        fs.addContentToFile("/file2.txt", "content");
        List<String> result = fs.ls("/");
        assertEquals(Arrays.asList("dir1", "dir2", "file1.txt", "file2.txt"), result);
    }
    
    // ===== CD TESTS =====
    
    @Test
    @Order(10)
    @DisplayName("cd: Absolute path")
    void testCdAbsolutePath() {
        fs.mkdir("/a/b/c");
        fs.cd("/a/b/c");
        assertEquals("/a/b/c", fs.pwd());
    }
    
    @Test
    @Order(11)
    @DisplayName("cd: Relative path")
    void testCdRelativePath() {
        fs.mkdir("/a/b/c");
        fs.cd("/a");
        fs.cd("b/c");
        assertEquals("/a/b/c", fs.pwd());
    }
    
    @Test
    @Order(12)
    @DisplayName("cd: Parent with ..")
    void testCdParent() {
        fs.mkdir("/a/b/c");
        fs.cd("/a/b/c");
        fs.cd("..");
        assertEquals("/a/b", fs.pwd());
    }
    
    @Test
    @Order(13)
    @DisplayName("cd: Multiple parents ../..")
    void testCdMultipleParents() {
        fs.mkdir("/a/b/c/d");
        fs.cd("/a/b/c/d");
        fs.cd("../..");
        assertEquals("/a/b", fs.pwd());
    }
    
    @Test
    @Order(14)
    @DisplayName("cd: Parent then sibling ../sibling")
    void testCdParentThenSibling() {
        fs.mkdir("/a/b");
        fs.mkdir("/a/c");
        fs.cd("/a/b");
        fs.cd("../c");
        assertEquals("/a/c", fs.pwd());
    }
    
    @Test
    @Order(15)
    @DisplayName("cd: Beyond root stays at root - critical edge case")
    void testCdBeyondRoot() {
        fs.cd("/");
        assertDoesNotThrow(() -> fs.cd(".."));
        assertEquals("/", fs.pwd());
        
        fs.mkdir("/a");
        fs.cd("/a");
        assertDoesNotThrow(() -> fs.cd("../.."));
        assertEquals("/", fs.pwd());
    }
    
    @Test
    @Order(16)
    @DisplayName("cd: Complex paths")
    void testCdComplexPaths() {
        fs.mkdir("/a/b/c/d");
        fs.mkdir("/a/x/y");
        fs.cd("/a/b/c");
        fs.cd("../../x/y");
        assertEquals("/a/x/y", fs.pwd());
    }
    
    @Test
    @Order(17)
    @DisplayName("cd: To root")
    void testCdToRoot() {
        fs.mkdir("/a/b/c");
        fs.cd("/a/b/c");
        fs.cd("/");
        assertEquals("/", fs.pwd());
    }
    
    @Test
    @Order(18)
    @DisplayName("cd: Non-existent throws exception")
    void testCdNonExistent() {
        assertThrows(PathNotFoundException.class, () -> fs.cd("/nonexistent"));
    }
    
    @Test
    @Order(19)
    @DisplayName("cd: To file throws exception")
    void testCdToFile() {
        fs.addContentToFile("/file.txt", "content");
        assertThrows(NotADirectoryException.class, () -> fs.cd("/file.txt"));
    }
    
    // ===== FILE OPERATION TESTS =====
    
    @Test
    @Order(20)
    @DisplayName("File: Create new file")
    void testCreateNewFile() {
        fs.addContentToFile("/file.txt", "hello");
        assertEquals("hello", fs.readContentFromFile("/file.txt"));
    }
    
    @Test
    @Order(21)
    @DisplayName("File: Append to existing")
    void testAppendToExisting() {
        fs.addContentToFile("/file.txt", "hello");
        fs.addContentToFile("/file.txt", " world");
        assertEquals("hello world", fs.readContentFromFile("/file.txt"));
    }
    
    @Test
    @Order(22)
    @DisplayName("File: Read non-existent throws exception")
    void testReadNonExistent() {
        assertThrows(PathNotFoundException.class, 
                () -> fs.readContentFromFile("/nonexistent.txt"));
    }
    
    @Test
    @Order(23)
    @DisplayName("File: Read directory throws exception")
    void testReadDirectory() {
        fs.mkdir("/dir");
        assertThrows(NotAFileException.class, 
                () -> fs.readContentFromFile("/dir"));
    }
    
    @Test
    @Order(24)
    @DisplayName("File: Create file with auto-parent creation")
    void testFileAutoParentCreation() {
        fs.addContentToFile("/a/b/c/file.txt", "content");
        assertEquals("content", fs.readContentFromFile("/a/b/c/file.txt"));
        List<String> result = fs.ls("/a/b/c");
        assertEquals(Arrays.asList("file.txt"), result);
    }
    
    @Test
    @Order(25)
    @DisplayName("File: Relative path operations")
    void testFileRelativePaths() {
        fs.mkdir("/a/b");
        fs.cd("/a");
        fs.addContentToFile("b/file.txt", "test");
        assertEquals("test", fs.readContentFromFile("b/file.txt"));
    }
    
    // ===== PWD TESTS =====
    
    @Test
    @Order(26)
    @DisplayName("pwd: Initial state is root")
    void testPwdInitial() {
        assertEquals("/", fs.pwd());
    }
    
    @Test
    @Order(27)
    @DisplayName("pwd: After cd operations")
    void testPwdAfterCd() {
        fs.mkdir("/a/b/c");
        fs.cd("/a/b");
        assertEquals("/a/b", fs.pwd());
        fs.cd("c");
        assertEquals("/a/b/c", fs.pwd());
    }
    
    // ===== EDGE CASE TESTS =====
    
    @Test
    @Order(28)
    @DisplayName("Edge: Multiple slashes in path")
    void testMultipleSlashes() {
        fs.mkdir("//a///b//c");
        List<String> result = fs.ls("/a/b");
        assertEquals(Arrays.asList("c"), result);
    }
    
    @Test
    @Order(29)
    @DisplayName("Edge: Dot navigation")
    void testDotNavigation() {
        fs.mkdir("/a/b");
        fs.cd("/a/./b");
        assertEquals("/a/b", fs.pwd());
    }
    
    @Test
    @Order(30)
    @DisplayName("Edge: Invalid path - null")
    void testInvalidPathNull() {
        assertThrows(InvalidPathException.class, () -> fs.mkdir(null));
        assertThrows(InvalidPathException.class, () -> fs.addContentToFile(null, "content"));
    }
    
    // ===== CONCURRENCY TESTS =====
    
    @Test
    @Order(31)
    @DisplayName("Concurrency: Concurrent mkdir operations")
    void testConcurrentMkdir() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    fs.mkdir("/dir" + index);
                    fs.mkdir("/shared/dir" + index);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        List<String> rootContents = fs.ls("/");
        assertTrue(rootContents.size() >= 10);
        
        List<String> sharedContents = fs.ls("/shared");
        assertEquals(10, sharedContents.size());
    }
    
    @Test
    @Order(32)
    @DisplayName("Concurrency: Concurrent read operations")
    void testConcurrentReads() throws InterruptedException {
        fs.mkdir("/a/b/c");
        fs.addContentToFile("/a/b/c/file.txt", "content");
        
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    String content = fs.readContentFromFile("/a/b/c/file.txt");
                    List<String> list = fs.ls("/a/b/c");
                    String pwd = fs.pwd();
                    if ("content".equals(content) && list.size() == 1) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(threadCount, successCount.get());
    }
    
    @Test
    @Order(33)
    @DisplayName("Concurrency: Concurrent file append")
    void testConcurrentFileAppend() throws InterruptedException {
        fs.addContentToFile("/shared.txt", "");
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    fs.addContentToFile("/shared.txt", String.valueOf(index));
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        String content = fs.readContentFromFile("/shared.txt");
        assertEquals(10, content.length());
        
        // Verify all digits are present
        for (int i = 0; i < 10; i++) {
            assertTrue(content.contains(String.valueOf(i)));
        }
    }
    
    @Test
    @Order(34)
    @DisplayName("Integration: Complex workflow")
    void testComplexWorkflow() {
        // Create directory structure
        fs.mkdir("/home/user/documents");
        fs.mkdir("/home/user/downloads");
        fs.mkdir("/var/log");
        
        // Navigate and create files
        fs.cd("/home/user");
        fs.addContentToFile("documents/readme.txt", "Documentation");
        fs.addContentToFile("downloads/file.dat", "Data");
        
        // Navigate using relative paths
        fs.cd("documents");
        assertEquals("/home/user/documents", fs.pwd());
        
        // Go back and check sibling
        fs.cd("../downloads");
        assertEquals("/home/user/downloads", fs.pwd());
        
        // Read file
        String content = fs.readContentFromFile("file.dat");
        assertEquals("Data", content);
        
        // Navigate to root and check structure
        fs.cd("/");
        List<String> rootContents = fs.ls("/");
        assertTrue(rootContents.contains("home"));
        assertTrue(rootContents.contains("var"));
        
        // Check nested listing
        List<String> userContents = fs.ls("/home/user");
        assertEquals(Arrays.asList("documents", "downloads"), userContents);
    }
}
