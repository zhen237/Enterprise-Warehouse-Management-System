# 企业仓库管理系统 - GitHub Codespaces 预览

## 快速预览步骤（3 步即可）

### 第 1 步：创建 Codespace

1. 打开本仓库页面
2. 点击绿色 **"Code"** 按钮 → 选择 **"Codespaces"** 标签
3. 点击 **"Create codespace on master"**（创建 Codespace）

### 第 2 步：等待环境自动配置

- Codespaces 会自动安装 Java 21、Maven、Node.js 20、MySQL 8.0
- 首次创建约 3-5 分钟

### 第 3 步：一键启动

在 Codespaces 终端执行：
```bash
bash .devcontainer/start.sh
```

等待脚本自动完成：
- ✅ MySQL 数据库初始化
- ✅ 后端 Spring Boot 启动（端口 8080）
- ✅ 前端 Vue 启动（端口 5173）

启动完成后，在 VS Code 底部的 **"Ports"** 标签页会显示 5173 端口，点击即可在浏览器中预览。

## 目录结构

```
.devcontainer/
├── devcontainer.json   # Codespaces 环境配置
├── start.sh            # 一键启动脚本
└── README.md           # 本说明文件
```

## 注意事项

- 免费版 Codespaces 有使用时长限制（每月 120 小时）
- 若停止使用，Codespace 会自动休眠；重新打开可继续使用
- 数据库数据在 Codespace 生命周期内保持，删除 Codespace 后数据会清除
