# GitHub Pages 部署教程

本文档指导你将项目前端部署到 GitHub Pages，获得免费的在线演示地址。

---

## 一、两种部署方式

| 方式 | 说明 | 适用场景 |
|------|------|----------|
| **方式一：GitHub Actions 自动部署** | push 到 master 分支自动部署 | 推荐，一劳永逸 |
| **方式二：本地脚本手动部署** | 双击 `deploy-pages.bat` | 不想用 GitHub Actions |

---

## 二、方式一：GitHub Actions 自动部署（推荐）

### 步骤 1：启用 GitHub Pages

1. 访问你的仓库：https://github.com/zhen237/Enterprise-Warehouse-Management-System
2. 点击顶部 **Settings**
3. 左侧菜单选择 **Pages**
4. **Source** 选择 **GitHub Actions**（而不是 Deploy from a branch）

### 步骤 2：推送代码触发部署

代码中已包含 `.github/workflows/deploy.yml` 配置文件，每次 push 到 master 分支会自动：

1. 检出代码
2. 安装 Node.js 依赖
3. 构建前端（base 路径自动设为 `/Enterprise-Warehouse-Management-System/`）
4. 上传构建产物
5. 部署到 GitHub Pages

### 步骤 3：查看部署结果

1. 点击仓库顶部的 **Actions** 标签
2. 可以看到正在运行的部署工作流
3. 等待状态变为 ✅ 绿色（约 2-3 分钟）
4. 部署完成后，在 Settings → Pages 查看部署地址

### 步骤 4：访问你的网站

```
https://zhen237.github.io/Enterprise-Warehouse-Management-System/
```

---

## 三、方式二：本地脚本手动部署

### 步骤 1：双击运行脚本

```
deploy-pages.bat
```

脚本会自动完成：
- 安装依赖
- 构建前端
- 创建 gh-pages 分支
- 推送到 GitHub

### 步骤 2：配置 GitHub Pages

1. 访问仓库 **Settings → Pages**
2. **Source** 选择 **Deploy from a branch**
3. **Branch** 选择 `gh-pages`
4. 点击 **Save**

### 步骤 3：访问网站

```
https://zhen237.github.io/Enterprise-Warehouse-Management-System/
```

---

## 四、更新部署

### 自动部署方式
直接 push 代码到 master 分支即可，GitHub Actions 会自动重新部署。

### 手动部署方式
1. 重新运行 `deploy-pages.bat`
2. 刷新 GitHub Pages 页面

---

## 五、重要说明

### 5.1 纯前端部署限制

GitHub Pages 只能部署**静态文件**，所以：

| 功能 | 是否可用 | 原因 |
|------|----------|------|
| 查看页面 UI | ✅ | 静态 HTML/CSS/JS |
| 查看菜单 | ✅ | 前端路由 |
| 登录 | ❌ | 没有后端校验 |
| 增删改数据 | ❌ | 没有后端 API |
| 显示商品列表 | ❌ | 数据从后端获取失败 |

### 5.2 如何让数据正常显示

**方案 A：Mock 数据（纯前端模拟）**

在前端代码中添加 mock 数据，模拟后端返回。适合课程演示。

**方案 B：前后端分离部署**

- 前端 → GitHub Pages
- 后端 → Railway / Render（需信用卡）
- 数据库 → Railway / Render

**方案 C：本地运行后端**

- 前端 → GitHub Pages
- 后端 → 本地运行 + 内网穿透（cpolar）
- 适合临时演示

### 5.3 base 路径说明

Vue 项目的 base 路径设置为 `/Enterprise-Warehouse-Management-System/`，这是因为：

- GitHub Pages 地址格式：`https://用户名.github.io/仓库名/`
- 所有静态资源必须从子路径加载
- 如果 base 设置错误，页面会显示空白

本地开发时 base 为 `/`（不需要仓库名前缀）。

### 5.4 常见问题

**Q1：页面显示 404？**
- 检查 GitHub Pages 的 Source 是否选择了正确的分支
- 确认 URL 中仓库名大小写正确

**Q2：页面显示空白？**
- 打开浏览器控制台，检查是否有资源加载 404
- 可能是 base 路径配置错误

**Q3：刷新页面后显示 404？**
- 脚本已自动创建 `404.html` 文件
- 如果仍然有问题，检查 `404.html` 是否在根目录

**Q4：API 请求失败？**
- 这是预期行为，因为 GitHub Pages 没有后端
- 可以用 Mock 数据或配置外部后端解决

---

## 六、后续升级方案

如果需要完整功能演示（包括数据增删改），可以考虑：

| 方案 | 成本 | 复杂度 | 说明 |
|------|------|--------|------|
| GitHub Pages + Mock 数据 | 免费 | 低 | 纯前端，数据写死 |
| GitHub Pages + Railway | 免费（需信用卡） | 中 | 前后端分离 |
| Vercel + Railway | 免费（需信用卡） | 中 | 体验更好 |
| 本地 + cpolar 内网穿透 | 免费 | 中 | 电脑需开机 |

---

## 七、文件清单

部署相关文件：

| 文件 | 说明 |
|------|------|
| `.github/workflows/deploy.yml` | GitHub Actions 自动部署配置 |
| `deploy-pages.bat` | Windows 本地部署脚本 |
| `frontend/vite.config.js` | 已修改支持动态 base 路径 |
| `vercel.json` | Vercel 配置（备用） |
