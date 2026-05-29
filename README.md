# Enterprise Warehouse Management System

A modern warehouse management system built with Vue 3 and Spring Boot.

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

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Admin |
| operator | operator123 | Operator |

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
