# Paper Hello Plugin Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a minimal Paper 1.20.x plugin with a /hello command and a player-join welcome message.

**Architecture:** A single JavaPlugin entrypoint registers one CommandExecutor and one Listener. Tests use JUnit 5 + Mockito to verify command and event behaviors without running a server.

**Tech Stack:** Java 17, Maven, Paper API, JUnit 5, Mockito.

### Task 1: Initialize Maven project skeleton

**Files:**
- Create: `pom.xml`
- Create: `src/main/resources/plugin.yml`
- Create: `src/test/java/com/example/paperhello/PaperHelloPluginTest.java`
- Create: `src/main/java/com/example/paperhello/PaperHelloPlugin.java`

**Step 1: Write the failing test**

```java
package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class PaperHelloPluginTest {
    @Test
    void pluginConstructs() {
        assertDoesNotThrow(PaperHelloPlugin::new);
    }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=PaperHelloPluginTest test`
Expected: FAIL with "cannot find symbol PaperHelloPlugin" or compilation error.

**Step 3: Write minimal implementation**

Create `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>paper-hello</artifactId>
  <version>1.0.0</version>
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <dependencies>
    <dependency>
      <groupId>io.papermc.paper</groupId>
      <artifactId>paper-api</artifactId>
      <version>1.20.4-R0.1-SNAPSHOT</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>5.10.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>5.10.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.2.5</version>
      </plugin>
    </plugins>
  </build>
</project>
```

Create `src/main/resources/plugin.yml`:

```yaml
name: PaperHello
version: 1.0.0
main: com.example.paperhello.PaperHelloPlugin
api-version: 1.20
commands:
  hello:
    description: Say hello
    usage: /hello
```

```java
package com.example.paperhello;

import org.bukkit.plugin.java.JavaPlugin;

public class PaperHelloPlugin extends JavaPlugin {
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=PaperHelloPluginTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add pom.xml src/main/java/com/example/paperhello/PaperHelloPlugin.java src/main/resources/plugin.yml src/test/java/com/example/paperhello/PaperHelloPluginTest.java
git commit -m "feat: bootstrap paper plugin"
```

### Task 2: Add /hello command with unit tests

**Files:**
- Create: `src/main/java/com/example/paperhello/HelloCommand.java`
- Modify: `src/main/java/com/example/paperhello/PaperHelloPlugin.java`
- Create: `src/test/java/com/example/paperhello/HelloCommandTest.java`

**Step 1: Write the failing test**

```java
package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class HelloCommandTest {
    @Test
    void sendsHelloMessage() {
        HelloCommand cmd = new HelloCommand();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        boolean result = cmd.onCommand(sender, command, "hello", new String[0]);

        assertTrue(result);
        verify(sender).sendMessage("Hello from PaperHello!");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=HelloCommandTest test`
Expected: FAIL with "cannot find symbol HelloCommand".

**Step 3: Write minimal implementation**

```java
package com.example.paperhello;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelloCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Hello from PaperHello!");
        return true;
    }
}
```

Modify `PaperHelloPlugin` to register the command in `onEnable()`:

```java
@Override
public void onEnable() {
    getCommand("hello").setExecutor(new HelloCommand());
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=HelloCommandTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/paperhello/HelloCommand.java src/main/java/com/example/paperhello/PaperHelloPlugin.java src/test/java/com/example/paperhello/HelloCommandTest.java

git commit -m "feat: add hello command"
```

### Task 3: Add join listener with unit tests

**Files:**
- Create: `src/main/java/com/example/paperhello/JoinListener.java`
- Modify: `src/main/java/com/example/paperhello/PaperHelloPlugin.java`
- Create: `src/test/java/com/example/paperhello/JoinListenerTest.java`

**Step 1: Write the failing test**

```java
package com.example.paperhello;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

class JoinListenerTest {
    @Test
    void welcomesPlayerOnJoin() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "");

        JoinListener listener = new JoinListener();
        listener.onPlayerJoin(event);

        verify(player).sendMessage("Welcome to the server!");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=JoinListenerTest test`
Expected: FAIL with "cannot find symbol JoinListener".

**Step 3: Write minimal implementation**

```java
package com.example.paperhello;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");
    }
}
```

Modify `PaperHelloPlugin` to register the listener in `onEnable()`:

```java
@Override
public void onEnable() {
    getCommand("hello").setExecutor(new HelloCommand());
    getServer().getPluginManager().registerEvents(new JoinListener(), this);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=JoinListenerTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/paperhello/JoinListener.java src/main/java/com/example/paperhello/PaperHelloPlugin.java src/test/java/com/example/paperhello/JoinListenerTest.java

git commit -m "feat: add join listener"
```
