# Enterprise Warehouse Management System

A modern warehouse management system built with Vue 3 and Spring Boot.

## 🌐 Online Demo

- **Live Demo (GitHub Pages)**: https://zhen237.github.io/Enterprise-Warehouse-Management-System/
- **Mock Demo**: Click "🚀 一键管理员演示" on login page to enter admin dashboard directly

> 💡 GitHub Pages demo uses Mock data (no backend). You can click the demo button on login page to enter directly, or use any username/password (min 3 chars).

## 🌟 Features

- 📦 **Product Management** - CRUD operations, category filtering
- 🏭 **Warehouse Management** - Warehouse info with stock count and total value
- 🔍 **Inventory Query** - Real-time inventory, price display, low stock alerts
- 📥 **Inbound Management** - Inbound applications, confirmations, auto-update inventory
- 📤 **Outbound Management** - Outbound applications, confirmations, auto-deduct inventory
- 📋 **Inventory Check** - Stocktaking records, variance handling
- 📊 **Reports & Statistics** - Data visualization and analytics

## 🛠 Tech Stack

| Frontend | Backend |
|----------|---------|
| Vue 3 | Spring Boot 3.2 |
| Vite 5 | Spring Security 6 |
| Element Plus 2.6 | Spring Data JPA |
| ECharts 5.5 | MySQL 8.0 |
| Axios | JWT Token |

## 🚀 Quick Start

**Requirements**: JDK 21+ / Node.js 18+ / MySQL 8.0+

```bash
# 1. Clone the repository
git clone https://github.com/zhen237/Enterprise-Warehouse-Management-System.git
cd Enterprise-Warehouse-Management-System

# 2. Create database
CREATE DATABASE warehouse_db CHARACTER SET utf8mb4;

# 3. Start backend
cd backend
mvn spring-boot:run

# 4. Start frontend
cd frontend
npm install
npm run dev
```

## 🔑 Test Accounts

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | admin123 | Admin | Full access |
| operator | operator123 | Operator | Inbound/Outbound/Check only |
| employee | employee123 | Employee | View only |

## ☁️ Deploy to Render (Free Tier)

This project includes a `render.yaml` Blueprint for one-click deployment:

1. Register at [render.com](https://render.com) (sign in with GitHub)
2. Dashboard → **New** → **Blueprint**
3. Select this repository, Render will auto-detect `render.yaml`
4. Click **Apply** — Render will create:
   - MySQL database
   - Spring Boot backend (Docker)
   - Vue static frontend
5. Wait ~5 minutes for the first build to complete

See [在线演示部署指南.md](在线演示部署指南.md) for detailed instructions.

## 📌 System Highlights

- 🎨 Modern UI with smooth animations
- 📱 Responsive layout
- 🔒 Role-based access control
- 📈 Low stock warning system
- 💰 Inventory value tracking
- 📊 Interactive charts

## 📂 Project Structure

```
├── backend/              # Spring Boot backend
├── frontend/             # Vue3 frontend
├── README.md             # English documentation
├── 部署指南.md           # Chinese deployment guide
└── 系统设计报告.md       # Chinese design document
```

## 📄 Documentation

- [Deployment Guide (中文)](部署指南.md)
- [System Design Report (中文)](系统设计报告.md)
