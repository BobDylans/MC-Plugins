# 倒计时播报与方块破坏播报设计文档

**日期：** 2026-02-05
**作者：** Claude Code
**目标：** 演示并发编程在 Minecraft 插件中的应用

---

## 功能概述

本设计实现两个功能：
1. **倒计时播报** - 玩家通过命令触发全服倒计时
2. **方块破坏播报** - 实时播报玩家破坏方块的行为，包含防刷屏机制

---

## 功能一：倒计时播报

### 需求描述

- **命令格式：** `/countdown <秒数>`
- **触发方式：** 玩家命令触发
- **播报范围：** 全服广播
- **消息格式：** 标准倒计时（10, 9, 8... 3, 2, 1, GO!）

### 架构设计

```
com.example.paperhello.countdown/
├── CountdownCommand.java          # 命令入口
├── CountdownManager.java          # 管理多个倒计时任务
└── CountdownTask.java             # 单个倒计时任务
```

#### CountdownCommand

**职责：**
- 解析命令参数（秒数）
- 调用 CountdownManager 启动倒计时
- 验证参数有效性

#### CountdownManager

**职责：**
- 管理多个并发倒计时任务
- 防止同一玩家重复启动
- 生成唯一任务 ID

**并发设计：**
```java
// 线程安全的任务存储
private final ConcurrentHashMap<Integer, CountdownTask> activeTasks;
private final ConcurrentHashMap<String, Integer> playerToTaskId;
private final AtomicInteger taskIdGenerator;
```

#### CountdownTask

**职责：**
- 执行倒计时逻辑
- 每秒发送广播消息
- 完成后清理资源

**并发设计：**
```java
// 继承 BukkitRunnable 使用服务器调度器
public class CountdownTask extends BukkitRunnable {
    private final AtomicInteger remainingSeconds;
    private final String playerName;

    @Override
    public void run() {
        // 在主线程安全地广播消息
        Bukkit.broadcast(Component.text(remainingSeconds.get()));
    }
}
```

### 并发编程要点

1. **ConcurrentHashMap** - 存储活跃任务，线程安全的读写
2. **AtomicInteger** - 原子操作的任务 ID 生成器和计数器
3. **BukkitScheduler** - 使用服务器内置调度器，避免自己创建线程池

### 测试策略

- 单个倒计时的正确性
- 多个玩家同时启动倒计时
- 防止重复启动

---

## 功能二：方块破坏播报

### 需求描述

- **播报内容：** 玩家 + 数量（如 "PlayerA 破坏了 5 个方块"）
- **播报范围：** 全服广播
- **播报频率：** 实时播报 + 防刷屏
- **覆盖范围：** 全部方块

### 架构设计

```
com.example.paperhello.blockbreak/
├── BlockBreakListener.java       # 事件监听器
├── SpamFilter.java               # 防刷屏过滤器
└── StatsCleaner.java              # 定期清理过期统计
```

#### BlockBreakListener

**职责：**
- 监听 `BlockBreakEvent`
- 调用 SpamFilter 检查是否应该播报
- 发送广播消息

```java
@EventHandler(priority = EventPriority.MONITOR)
public void onBlockBreak(BlockBreakEvent event) {
    if (spamFilter.shouldBroadcast(event.getPlayer())) {
        int count = spamFilter.getCountAndReset(event.getPlayer());
        Bukkit.broadcast(
            Component.text(event.getPlayer().getName() + " 破坏了 " + count + " 个方块")
        );
    }
}
```

#### SpamFilter

**职责：**
- 统计每个玩家的破坏次数
- 实现时间窗口限流
- 提供线程安全的统计接口

**并发设计：**
```java
public class SpamFilter {
    // 线程安全的玩家统计存储
    private final ConcurrentHashMap<String, PlayerStats> playerStats;
    private final ScheduledExecutorService cleanerExecutor;

    // 玩家统计数据
    private static class PlayerStats {
        private final AtomicInteger count;          // 当前窗口内的破坏次数
        private final Long lastBroadcastTime;       // 上次播报时间（volatile）
    }
}
```

**核心逻辑：**
- 每次破坏时，增加计数
- 检查距离上次播报是否超过时间窗口（3秒）
- 超过窗口则允许播报并重置计数

#### StatsCleaner

**职责：**
- 后台定期清理不活跃玩家的统计数据
- 防止内存泄漏

```java
public class StatsCleaner {
    private final ScheduledExecutorService executor;

    public void startCleanup() {
        executor.scheduleAtFixedRate(() -> {
            // 清理超过阈值未活跃的玩家
        }, 60, 60, TimeUnit.SECONDS);
    }
}
```

### 并发编程要点

1. **ConcurrentHashMap** - 玩家统计数据的线程安全存储
2. **AtomicInteger** - 破坏计数的原子操作
3. **volatile** - 上次播报时间的可见性保证
4. **ScheduledExecutorService** - 后台清理线程
5. **主线程与后台线程并发访问** - 需要注意数据一致性

### 防刷屏策略

- **时间窗口：** 3秒
- **播报逻辑：** 窗口内累加计数，窗口到期时播报总次数
- **自动清理：** 长期不活跃的玩家数据自动删除

### 测试策略

- 单个玩家破坏方块的播报
- 多个玩家同时破坏方块
- 防刷屏机制验证
- 内存泄漏验证（清理机制）

---

## 技术栈

- **Paper API** 1.20.4
- **JDK** 21
- **并发工具类：**
  - `ConcurrentHashMap`
  - `AtomicInteger`
  - `ScheduledExecutorService`
  - `volatile`
  - `synchronized`（必要时）

---

## 实现步骤

### 第一阶段：倒计时播报
1. 创建包结构 `com.example.paperhello.countdown`
2. 实现 `CountdownTask`
3. 实现 `CountdownManager`
4. 实现 `CountdownCommand`
5. 在 `plugin.yml` 注册命令
6. 在主类注册命令和调度器
7. 编写单元测试

### 第二阶段：方块破坏播报
1. 创建包结构 `com.example.paperhello.blockbreak`
2. 实现 `SpamFilter`（核心并发逻辑）
3. 实现 `StatsCleaner`
4. 实现 `BlockBreakListener`
5. 在主类注册事件和清理器
6. 编写单元测试

### 第三阶段：集成测试
1. 多玩家并发倒计时测试
2. 多玩家并发破坏方块测试
3. 压力测试（验证性能和内存）

---

## 注意事项

1. **主线程安全** - Bukkit API 调用必须在主线程
2. **资源清理** - 插件禁用时需要关闭所有调度器
3. **异常处理** - 异常情况下确保资源不会泄漏
4. **测试覆盖** - 重点测试并发场景和边界条件

---

## 后续优化方向

1. 将硬编码值提取为配置项（时间窗口、清理间隔等）
2. 添加命令权限控制
3. 支持自定义消息格式
4. 添加倒计时取消功能
