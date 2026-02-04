# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PaperHello is a PaperMC 1.20.x Minecraft server plugin written in Java 17. It follows Test-Driven Development (TDD) practices with Maven as the build system.

## Build & Development Commands

### Maven Commands
```bash
# Build the plugin (includes tests)
mvn clean package

# Compile without building
mvn compile

# Build without running tests
mvn package -DskipTests

# Run tests only
mvn test

# Run specific test
mvn -Dtest=HelloCommandTest test

# Run with coverage report
mvn test surefire-report:report
```

### Running the Plugin
1. Build with `mvn clean package`
2. Copy `target/paper-hello-1.0.0.jar` to PaperMC server's `plugins/` directory
3. Restart server

## Architecture

### Core Components
- **PaperHelloPlugin**: Main plugin class extending JavaPlugin
- **HelloCommand**: Command executor for `/hello`
- **JoinListener**: Event listener for player joins

### Package Structure
```
com.example.paperhello/
├── PaperHelloPlugin.java  # Main plugin entry point
├── HelloCommand.java      # Command implementation
└── JoinListener.java       # Event listener
```

### TDD Workflow
- Write tests first (RED - test fails)
- Implement minimal code to pass tests (GREEN)
- Refactor if needed (IMPROVE)
- Maintain 80%+ test coverage

### Testing Patterns
- Uses JUnit 5 with Mockito
- Mocks Bukkit objects (Player, CommandSender, etc.)
- Tests located in `src/test/java/`
- Mirror source structure for test classes

## Key Conventions

### Plugin Configuration
- API version: 1.20
- Main class: `com.example.paperhello.PaperHelloPlugin`
- Commands defined in `plugin.yml`

### Dependencies
- Paper API 1.20.4-SNAPSHOT (provided scope)
- JUnit 5.10.2 (test scope)
- Mockito 5.10.0 (test scope)

### Code Quality
- Java 17 target/source compatibility
- UTF-8 encoding
- No deep nesting
- Immutable patterns
- Small focused methods

## Development Notes

- Plugin automatically loads and registers components in `onEnable()`
- Join listener sends "Welcome to the server!" message
- Hello command responds with "Hello from PaperHello!"
- All tests should pass before committing
- Follow conventional commit format