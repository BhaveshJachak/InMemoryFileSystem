# In-Memory File System

A thread-safe, in-memory file system implementation in Java, similar to [LeetCode 588](https://leetcode.com/problems/design-in-memory-file-system/).

## Features

- `mkdir` - Create directories (with auto-creation of parent directories)
- `ls` - List directory contents
- `cd` - Change directory (supports absolute, relative, and `..` navigation)
- `addContentToFile` - Create/append content to files
- `readContentFromFile` - Read file contents
- `pwd` - Print working directory

## Design Patterns

- Singleton Pattern
- Facade Pattern
- Command Pattern
- Factory Pattern
- Composite Pattern

## Thread Safety

- ReentrantReadWriteLock for state management
- TreeMap with ReadWriteLock for directory children

## Coming Soon

Full implementation via Pull Request.