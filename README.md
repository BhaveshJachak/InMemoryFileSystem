# In-Memory File System

A thread-safe, in-memory file system implementation in Java, similar to [LeetCode 588](https://leetcode.com/problems/design-in-memory-file-system/) with extended `cd` command support.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Tests](https://img.shields.io/badge/tests-34%20passed-success.svg)]()
[![Java](https://img.shields.io/badge/Java-11-orange.svg)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)]()

## Features

| Command | Description | Examples |
|---------|-------------|----------|
| `mkdir` | Create directory path (auto-creates parent dirs like `mkdir -p`) | `mkdir /a/b/c` |
| `ls` | List directory contents OR return file name if path is file | `ls /`, `ls /a/b/file.txt` |
| `cd` | Change current working directory | `cd /a`, `cd ..`, `cd ../b` |
| `addContentToFile` | Create file / Append content to file | `addContentToFile /a/b/c/file.txt "hello"` |
| `readContentFromFile` | Read content from file | `readContentFromFile /a/b/c/file.txt` |
| `pwd` | Print current working directory | `pwd` |

## Design Patterns

This project demonstrates the implementation of 5 design patterns:

1. **Singleton Pattern** - FileSystem class ensures single instance
2. **Facade Pattern** - FileSystem provides simple API for complex operations
3. **Command Pattern** - All operations encapsulated as command objects
4. **Factory Pattern** - CommandFactory creates command instances
5. **Composite Pattern** - FileSystemNode hierarchy (DirectoryNode, FileNode)

## Architecture

### Data Structure: Path-Segment Trie
- Each node represents a directory or file
- Uses **TreeMap with ReentrantReadWriteLock** (not ConcurrentSkipListMap)
- Provides O(k) time complexity where k is path depth

### Why TreeMap + Lock over ConcurrentSkipListMap?
1. **Strong consistency** - `ls()` returns consistent snapshot
2. **Compound atomicity** - Multiple operations in single lock
3. **Lower memory footprint** - Red-Black tree vs Skip List
4. **Explicit control** - Clearer thread-safety reasoning
5. **Better for interviews** - Demonstrates deeper understanding

### Time Complexities

| Operation | Time Complexity |
|-----------|-----------------|
| mkdir | O(k) where k = path depth |
| ls | O(k + n) where n = children count |
| cd | O(k) |
| addContentToFile | O(k + c) where c = content length |
| readContentFromFile | O(k) |

## Thread Safety

- **ReentrantReadWriteLock** for state management (currentDirectory, currentPathSegments)
- **TreeMap with ReadWriteLock** for directory children (sorted and thread-safe)
- **Synchronized methods** for file content operations
- **Atomic cd operations** using write locks

## Project Structure

```
src/main/java/filesystem/
├── FileSystem.java                 # Singleton + Facade + Core Trie logic
├── Main.java                       # Interactive CLI
├── node/
│   ├── NodeType.java               # Enum (FILE, DIRECTORY)
│   ├── FileSystemNode.java         # Abstract base (Composite Pattern)
│   ├── DirectoryNode.java          # Directory with TreeMap + ReadWriteLock
│   └── FileNode.java               # File with synchronized content
├── command/
│   ├── Command.java                # Command interface
│   ├── MkdirCommand.java
│   ├── LsCommand.java
│   ├── CdCommand.java
│   ├── AddContentCommand.java
│   ├── ReadContentCommand.java
│   └── PwdCommand.java
├── factory/
│   └── CommandFactory.java         # Factory Pattern
├── exception/
│   ├── FileSystemException.java    # Base exception
│   ├── PathNotFoundException.java
│   ├── InvalidPathException.java
│   ├── NotADirectoryException.java
│   └── NotAFileException.java
└── util/
    └── PathUtils.java              # Path parsing utilities

src/test/java/filesystem/
└── FileSystemTest.java             # 34 comprehensive tests
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Building the Project

```bash
# Clone the repository
git clone https://github.com/BhaveshJachak/InMemoryFileSystem.git
cd InMemoryFileSystem

# Compile the project
mvn clean compile

# Run tests
mvn test

# Run the interactive CLI
mvn exec:java -Dexec.mainClass="filesystem.Main"
```

### Running Tests

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -X
```

All 34 tests include:
- mkdir tests (single, nested, siblings, idempotent)
- ls tests (root, empty, file, current directory, mixed content)
- cd tests (absolute, relative, .., ../.., ../sibling, beyond root, to root, non-existent, to file)
- File operation tests (create, append, read non-existent, read directory)
- Concurrency tests (concurrent mkdir, reads, file append)

## Usage Examples

### Programmatic Usage

```java
FileSystem fs = FileSystem.getInstance();

// Create directories
fs.mkdir("/home/user/documents");
fs.mkdir("/var/log");

// Change directory
fs.cd("/home/user");
System.out.println(fs.pwd()); // Output: /home/user

// Create files
fs.addContentToFile("documents/readme.txt", "Hello World");

// Read files
String content = fs.readContentFromFile("documents/readme.txt");
System.out.println(content); // Output: Hello World

// List contents
List<String> files = fs.ls("documents");
System.out.println(files); // Output: [readme.txt]

// Navigate using relative paths
fs.cd("documents");
fs.cd("..");
fs.cd("../.."); // Goes to root
```

### Interactive CLI

```bash
$ mvn exec:java -Dexec.mainClass="filesystem.Main"

/ $ mkdir /home/user/documents
/ $ ls /
home
/ $ cd /home/user
/home/user $ mkdir downloads
/home/user $ ls
documents
downloads
/home/user $ touch documents/readme.txt "Hello World"
/home/user $ cat documents/readme.txt
Hello World
/home/user $ cd documents
/home/user/documents $ pwd
/home/user/documents
/home/user/documents $ cd ../downloads
/home/user/downloads $ pwd
/home/user/downloads
/home/user/downloads $ exit
```

## Edge Cases Handled

1. **mkdir** - Idempotent (creating existing directory does not fail)
2. **cd .. beyond root** - Stays at root (does not throw error)
3. **cd supports**: absolute (`/a/b`), relative (`a/b`), parent (`..`, `../..`, `../sibling`)
4. **ls on file** - Returns `[filename]`, on directory returns sorted contents
5. **Path validation** - Handles null, empty, invalid characters
6. **File vs Directory conflicts** - Cannot create file where directory exists and vice versa

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by [LeetCode Problem 588](https://leetcode.com/problems/design-in-memory-file-system/)
- Implements best practices for thread-safe Java programming
- Demonstrates proper use of design patterns in real-world scenarios