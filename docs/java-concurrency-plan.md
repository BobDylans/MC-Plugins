# Java 并发编程学习规划

基于 PaperHello 插件项目的实战学习路径

## 学习目标

- 掌握 Java 并发编程核心概念
- 熟悉 PaperMC 插件中的异步任务机制
- 能够独立设计并实现线程安全的插件功能

---

## 阶段 1: PaperMC 异步任务基础

### 学习内容

| 主题 | 核心概念 | 练习 |
|------|----------|------|
| 主线程 vs 异步线程 | Bukkit 主线程（Server Tick）、异步执行 | 理解 PaperMC 的线程模型 |
| 异步任务 | `runTaskAsync`, `runTaskLaterAsync` | 实现异步日志记录 |
| 定时任务 | `runTaskTimer`, `runTaskTimerAsync` | 实现定时广播 |

### 实战练习

1. **异步日志系统**
   - 将玩家加入事件日志改为异步写入
   - 使用 `runTaskAsynchronously` 避免阻塞主线程

2. **定时问候**
   - 每隔 5 分钟向所有在线玩家发送欢迎消息
   - 使用 `runTaskTimerAsync`

### 参考代码

```java
// 异步执行任务
getServer().getScheduler().runTaskAsynchronously(this, () -> {
    // 耗时操作（如文件 I/O、网络请求）
    logPlayerJoin(player.getName());
});
```

---

## 阶段 2: Java 核心并发工具

### 学习内容

| 主题 | 核心概念 | 练习 |
|------|----------|------|
| CompletableFuture | 链式异步编程、异常处理 | 实现异步数据加载 |
| ExecutorService | 线程池管理、任务提交 | 创建自定义线程池 |
| ConcurrentHashMap | 线程安全 Map | 实现玩家数据缓存 |
| Atomic 类 | 原子操作 | 实现计数器 |

### 实战练习

1. **异步玩家数据加载**
   ```java
   CompletableFuture<PlayerData> loadPlayerData(String uuid) {
       return CompletableFuture.supplyAsync(() -> {
           // 从数据库或文件加载
           return readFromFile(uuid);
       }).thenApply(data -> {
           // 数据处理
           return processData(data);
       }).exceptionally(e -> {
           // 错误处理
           getLogger().warning("加载失败: " + e.getMessage());
           return null;
       });
   }
   ```

2. **玩家在线计数器**
   - 使用 `AtomicInteger` 统计在线玩家数
   - Join/Quit 事件中更新

3. **数据缓存系统**
   - 使用 `ConcurrentHashMap` 缓存玩家数据
   - 实现自动过期机制

---

## 阶段 3: 同步与锁机制

### 学习内容

| 主题 | 核心概念 | 练习 |
|------|----------|------|
| ReentrantLock | 可重入锁、公平锁 | 保护共享资源 |
| ReadWriteLock | 读写分离锁 | 优化读多写少场景 |
| Condition | 条件变量 | 实现生产者-消费者 |
| synchronized | 内置锁 | 简单同步场景 |

### 实战练习

1. **玩家交易系统**
   - 使用 `ReentrantLock` 保护玩家库存
   - 实现原子性的交易操作

2. **数据仓库**
   - 使用 `ReadWriteLock` 优化读取性能
   - 写操作加排他锁，读操作加共享锁

3. **任务队列**
   - 使用 `BlockingQueue` 实现异步任务队列
   - 多个 worker 线程消费任务

---

## 阶段 4: 综合实战项目

### 项目：玩家数据缓存系统

#### 功能需求

1. **异步加载**
   - 玩家加入时异步加载数据
   - 避免阻塞主线程

2. **本地缓存**
   - 使用 `ConcurrentHashMap` 缓存数据
   - 实现过期和淘汰策略

3. **异步保存**
   - 玩家退出时异步保存数据
   - 使用定时批量保存

4. **线程安全**
   - 保证缓存操作的原子性
   - 处理并发访问

#### 架构设计

```
PlayerJoinEvent
       ↓
   检查缓存 (ConcurrentHashMap)
       ↓
   未命中 → 异步加载 → CompletableFuture
       ↓
   缓存更新 → 原子操作

PlayerQuitEvent
       ↓
   标记待保存 → 加入队列
       ↓
   定时批量保存 → 线程池
```

---

## 学习资源

### 官方文档
- [Java Concurrency in Practice](https://jcip.net/) - 经典书籍
- [PaperMC Javadoc](https://jd.papermc.io/paper/1.20/) - Bukkit API 文档

### 推荐阅读
- 《Java 并发编程实战》
- 《深入理解 Java 虚拟机》（JMM 章节）

### 在线资源
- [Oracle Java 并发教程](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Baeldung Java 并发指南](https://www.baeldung.com/java-concurrency)

---

## 学习检查清单

### 阶段 1
- [ ] 理解 Bukkit 主线程和异步线程的区别
- [ ] 能够使用 `runTaskAsync` 执行异步任务
- [ ] 能够使用 `runTaskTimerAsync` 创建定时任务
- [ ] 实现异步日志记录功能

### 阶段 2
- [ ] 理解 `CompletableFuture` 的链式调用
- [ ] 能够创建和管理线程池
- [ ] 理解 `ConcurrentHashMap` 的线程安全特性
- [ ] 实现异步数据加载和缓存

### 阶段 3
- [ ] 理解 `ReentrantLock` 的使用场景
- [ ] 理解读写锁的优势
- [ ] 能够使用 `Condition` 实现线程间通信
- [ ] 实现线程安全的共享资源访问

### 阶段 4
- [ ] 完成玩家数据缓存系统
- [ ] 所有测试通过
- [ ] 代码符合最佳实践

---

## 注意事项

### PaperMC 线程安全规则

1. **主线程操作**
   - 所有 Bukkit API 调用必须在主线程执行
   - 异步任务中不能调用 Bukkit API

2. **跨线程通信**
   - 使用 `runTask` 将操作切回主线程
   - 使用 `CompletableFuture.thenRunAsync(() -> plugin.getServer().getScheduler().runTask(plugin, ...))`

3. **共享数据**
   - 使用线程安全的集合类
   - 对共享资源加锁保护

### 常见陷阱

- 在异步线程中调用 Bukkit API → 抛出 `AsyncChatEvent`
- 阻塞主线程 → 服务器卡顿
- 忘记加锁 → 数据竞争
- 死锁 → 服务器无响应

---

## 进度追踪

| 阶段 | 状态 | 完成时间 |
|------|------|----------|
| 阶段 1: PaperMC 异步任务基础 | ⬜ 待开始 | - |
| 阶段 2: Java 核心并发工具 | ⬜ 待开始 | - |
| 阶段 3: 同步与锁机制 | ⬜ 待开始 | - |
| 阶段 4: 综合实战项目 | ⬜ 待开始 | - |
