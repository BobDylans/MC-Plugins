# PaperHello 插件增强计划

**日期：** 2026-02-06
**作者：** Claude Code
**目标：** 完善插件功能，提升用户体验和代码质量

---

## 当前状态

### 已实现功能

| 功能 | 状态 | 描述 |
|------|--------|------|
| `/hello` 命令 | ✅ 已完成 | 简单的问候命令 |
| `/countdown` 命令 | ✅ 已完成 | 全服倒计时播报 |
| 玩家加入欢迎 | ✅ 已完成 | 玩家进入时发送欢迎消息 |
| 方块破坏播报 | ✅ 已完成 | 带防刷屏机制的方块统计 |
| 定时广播 | ✅ 已完成 | 定时公告在线人数 |

### 存在的问题

1. **硬编码配置** - 时间窗口、清理间隔等值硬编码在代码中
2. **缺少配置文件** - 无法通过 config.yml 调整插件行为
3. **无权限控制** - 任何玩家都能使用命令
4. **缺少取消功能** - 倒计时启动后无法取消
5. **消息格式固定** - 无法自定义广播消息格式
6. **无数据持久化** - 统计数据重启后丢失

---

## 增强计划

### 阶段 1: 配置系统

#### 目标
- 创建 `config.yml` 配置文件
- 将硬编码值提取为可配置项
- 支持热重载配置

#### 配置项设计

```yaml
# 倒计时配置
countdown:
  max-seconds: 300         # 最大倒计时秒数
  min-seconds: 1           # 最小倒计时秒数
  message-format: "玩家 {player} 的倒计时: {seconds} 秒"
  final-message: "GO!"

# 方块破坏播报配置
block-break:
  enabled: true             # 是否启用
  broadcast-window: 3000    # 广播时间窗口（毫秒）
  cleanup-interval: 60000    # 清理间隔（毫秒）
  message-format: "{player} 破坏了 {count} 个方块"

# 定时广播配置
scheduled-broadcast:
  enabled: true             # 是否启用
  interval-minutes: 5        # 广播间隔（分钟）
  message-format: "欢迎来到 PaperHello 服务器！当前在线人数: {count} 人"

# 权限配置
permissions:
  countdown:
    usage: "paperhello.countdown.use"
  block-break:
    view: "paperhello.blockbreak.view"
```

#### 实现步骤

1. 创建 `config.yml` 模板文件
2. 创建 `ConfigManager` 类管理配置加载
3. 修改 `SpamFilter` 使用配置的时间窗口
4. 修改 `StatsCleaner` 使用配置的清理间隔
5. 添加 `/reload` 命令重新加载配置
6. 编写配置加载测试

---

### 阶段 2: 权限系统

#### 目标
- 为命令添加权限控制
- 支持权限节点
- 无权限时提示友好消息

#### 权限节点设计

| 命令 | 权限节点 | 默认 |
|--------|-----------|--------|
| `/countdown` | `paperhello.countdown.use` | 所有玩家 |
| `/countdown cancel` | `paperhello.countdown.cancel` | 所有玩家 |
| `/countdown others` | `paperhello.countdown.others` | OP |
| `/hello` | `paperhello.hello.use` | 所有玩家 |
| `/ph reload` | `paperhello.reload` | OP |

#### 实现步骤

1. 在 `plugin.yml` 中定义权限节点
2. 修改 `CountdownCommand` 检查权限
3. 修改 `HelloCommand` 检查权限
4. 添加无权限时的友好提示
5. 编写权限测试

---

### 阶段 3: 倒计时增强

#### 目标
- 添加取消倒计时功能
- 支持为其他玩家启动倒计时
- 支持静默模式（不广播，仅自己可见）
- 倒计时完成时执行动作（烟花、声音等）

#### 命令设计

```bash
# 基础用法
/countdown 10

# 取消倒计时
/countdown cancel

# 为其他玩家启动倒计时（需要权限）
/countdown Steve 10

# 静默模式
/countdown 10 -s
```

#### 实现步骤

1. 扩展 `CountdownCommand` 支持多参数
2. 添加 `cancelCountdown` 方法到 `CountdownManager`
3. 添加 `silient` 参数支持
4. 添加倒计时完成特效（烟花）
5. 更新帮助消息
6. 编写测试

---

### 阶段 4: 数据持久化

#### 目标
- 保存玩家统计到文件
- 服务器重启后恢复数据
- 支持数据导出/导入

#### 存储格式

```json
{
  "version": "1.0",
  "players": {
    "uuid-of-player": {
      "name": "Steve",
      "totalBlocksBroken": 1234,
      "totalCountdowns": 5,
      "lastSeen": 1700000000000,
      "joinCount": 10
    }
  }
}
```

#### 实现步骤

1. 创建 `PlayerStats` 数据模型
2. 创建 `StatsRepository` 处理存储
3. 在玩家退出时保存数据
4. 在玩家加入时加载数据
5. 添加 `/stats` 命令查看统计
6. 异步保存避免阻塞主线程
7. 编写数据持久化测试

---

### 阶段 5: 统计增强

#### 目标
- 更多统计维度
- 排行榜系统
- 实时统计查看

#### 新增统计

```bash
# 查看个人统计
/stats
# 输出：
# 你的统计：
#   破坏方块总数: 1,234
#   发起倒计时: 5 次
#   加入服务器: 10 次
#   最后在线: 2 小时前

# 查看排行
/stats top
# 输出：
# 🏆 方块破坏排行榜:
#   1. Steve - 1,234
#   2. Alex - 987
#   3. Notch - 654
```

#### 实现步骤

1. 扩展 `PlayerStats` 模型
2. 实现 `StatsManager` 管理所有玩家统计
3. 添加 `/stats` 命令
4. 添加 `/stats top` 命令
5. 使用分页显示长列表
6. 缓存排行榜结果
7. 编写测试

---

### 阶段 6: 聊天增强

#### 目标
- 添加聊天气泡/格式化
- 玩家昵称前缀
- 聊天颜色支持

#### 功能设计

```yaml
chat:
  enabled: true
  format: "<{rank}{name}> {message}"
  ranks:
    default: "&7[玩家]&f"
    vip: "&6[VIP]&f"
    op: "&c[管理员]&f"
```

#### 实现步骤

1. 监听 `AsyncPlayerChatEvent`
2. 实现前缀系统
3. 支持颜色代码
4. 添加 `/nick` 命令设置昵称
5. 实现权限组前缀
6. 编写测试

---

### 阶段 7: 传送与回家

#### 目标
- `/home` 命令回家
- `/sethome` 设置家
- `/tpa` 传送请求
- `/tpaccept` 接受传送

#### 命令设计

```bash
/sethome              # 设置当前位置为家
/home                 # 传送到家
/home [name]           # 传送到指定名称的家
/home list            # 列出所有家

/tpa [player]          # 发送传送请求
/tpaccept [player]      # 接受传送请求
/tpdeny [player]       # 拒绝传送请求
```

#### 实现步骤

1. 创建 `Home` 数据模型
2. 创建 `HomeManager` 管理家数据
3. 实现 `/sethome` 命令
4. 实现 `/home` 命令
5. 实现 `/tpa` 传送系统
6. 添加传送冷却时间
7. 数据持久化
8. 编写测试

---

## 技术改进

### 代码质量

| 改进项 | 优先级 | 说明 |
|---------|---------|------|
| 单元测试覆盖率 | 高 | 目标 80%+ |
| 集成测试 | 高 | 测试组件交互 |
| 异常处理 | 中 | 完善错误处理和日志 |
| 文档注释 | 中 | 补充 JavaDoc |
| 代码格式化 | 低 | 统一代码风格 |

### 性能优化

| 优化项 | 优先级 | 说明 |
|---------|---------|------|
| 配置热重载 | 高 | 避免重启服务器 |
| 异步数据保存 | 高 | 减少主线程阻塞 |
| 统计缓存 | 中 | 减少数据库查询 |
| 对象池 | 低 | 减少对象创建 |

---

## 实施顺序

### 第一周：配置系统 + 权限系统
- Day 1-2: 配置系统
- Day 3-4: 权限系统
- Day 5-7: 测试与优化

### 第二周：倒计时增强 + 数据持久化
- Day 1-3: 倒计时取消等功能
- Day 4-7: 数据持久化系统

### 第三周：统计增强
- Day 1-5: 排行榜系统
- Day 6-7: 测试与优化

### 第四周：其他功能
- Day 1-3: 聊天增强
- Day 4-7: 传送系统（可选）

---

## 依赖项

### Paper API 版本
- 当前: 1.20.4-SNAPSHOT
- 建议: 保持最新稳定版

### 第三方库（可选）

| 库 | 用途 | 是否必需 |
|-----|--------|---------|
| LuckPerms | 权限管理 | 否（可选） |
| HikariCP | 数据库连接池 | 否（未来） |
| Vault | 经济系统 | 否（未来） |

---

## 测试策略

### 单元测试
- 每个 Manager 类 80%+ 覆盖率
- Mock Bukkit 对象
- 测试边界条件和异常情况

### 集成测试
- 端到端功能测试
- 多玩家并发测试
- 压力测试（100+ 玩家）

### 手动测试清单
- [ ] 配置加载正确
- [ ] 权限控制有效
- [ ] 倒计时可以取消
- [ ] 数据持久化正常
- [ ] 排行榜显示正确
- [ ] 聊天格式正常
- [ ] 传送功能正常
- [ ] 服务器重启数据不丢失

---

## 发布计划

### 版本规划

| 版本 | 内容 | 预计日期 |
|------|--------|---------|
| v1.1.0 | 配置系统 + 权限系统 | 1周后 |
| v1.2.0 | 倒计时增强 + 数据持久化 | 2周后 |
| v1.3.0 | 统计增强（排行榜） | 3周后 |
| v1.4.0 | 聊天 + 传送系统 | 4周后 |

### 发布检查清单
- [ ] 所有测试通过
- [ ] 配置文件模板完整
- [ ] README 更新
- [ ] 变更日志编写
- [ ] 构建成功
- [ ] Spigot/Bukkit 发布

---

## 学习收获

### 并发编程深化

在实现新功能时，将练习以下并发编程概念：

| 概念 | 应用场景 |
|--------|---------|
| ReadWriteLock | 玩家数据缓存系统 |
| BlockingQueue | 异步任务队列 |
| CompletableFuture | 异步数据加载 |
| Semaphore | 传送请求限流 |
| Phaser | 多阶段倒计时 |

---

## 注意事项

1. **向后兼容** - 配置文件升级时不丢失用户设置
2. **降级处理** - 外部依赖（如权限插件）缺失时正常工作
3. **性能监控** - 关注命令执行时间和内存使用
4. **用户反馈** - 收集玩家建议持续改进

---

## 资源参考

### PaperMC 文档
- [PaperMC Wiki](https://docs.papermc.io/)
- [Bukkit API Javadoc](https://jd.papermc.io/paper/1.20/)

### 设计模式
- [设计模式教程](https://refactoring.guru/)
- Minecraft 插件最佳实践

---

**文档版本：** 1.0
**最后更新：** 2026-02-06
