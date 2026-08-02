#!/bin/bash
set -e

WORKSPACE="/workspaces/Enterprise-Warehouse-Management-System"
BACKEND_DIR="$WORKSPACE/backend"
FRONTEND_DIR="$WORKSPACE/frontend"

echo "========================================"
echo " 企业仓库管理系统 - 一键启动脚本"
echo "========================================"
echo ""

echo "[1/4] 初始化 MySQL 数据库..."

if ! command -v mysqld &> /dev/null; then
    echo "  未检测到 MySQL，正在安装..."
    apt-get update -y > /dev/null 2>&1
    apt-get install -y mysql-server > /dev/null 2>&1 || apt-get install -y mariadb-server > /dev/null 2>&1
fi

service mysql start 2>/dev/null || mysqld_safe --skip-grant-tables &
sleep 3

mysql -u root -e "CREATE DATABASE IF NOT EXISTS warehouse_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
if mysql -u root warehouse_db -e "SELECT 1" 2>/dev/null; then
    echo "  数据库已就绪"
else
    echo "  尝试设置 root 密码..."
    mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;" 2>/dev/null || \
    mysql -u root -e "SET PASSWORD FOR 'root'@'localhost' = PASSWORD('root'); FLUSH PRIVILEGES;" 2>/dev/null || true
fi

if [ -f "$WORKSPACE/database-init.sql" ]; then
    mysql -u root -proot warehouse_db < "$WORKSPACE/database-init.sql" 2>/dev/null && \
        echo "  数据库初始化脚本已执行" || echo "  数据库可能已有数据，跳过初始化"
fi

echo ""
echo "[2/4] 更新后端配置（Codespaces 环境）..."
mkdir -p "$BACKEND_DIR/src/main/resources"
cat > "$BACKEND_DIR/src/main/resources/application.yml" << 'EOF'
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    com.example.warehouse: INFO
EOF

echo ""
echo "[3/4] 启动后端服务..."
cd "$BACKEND_DIR"
mvn spring-boot:run > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "  后端启动中 (PID: $BACKEND_PID)..."

echo ""
echo "[4/4] 启动前端服务..."
cd "$FRONTEND_DIR"
npm run dev -- --host 0.0.0.0 > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  前端启动中 (PID: $FRONTEND_PID)..."

sleep 8

echo ""
echo "========================================"
echo " 启动完成！"
echo "========================================"
echo ""
echo " 前端地址："
echo "  点击 VS Code 底部 [端口] 标签，找到 5173 端口"
echo "  点击右侧 '在浏览器中打开' 图标即可预览"
echo ""
echo " 如需分享给他人："
echo "  在端口标签中右键 5173 -> 端口可见性 -> 公开"
echo "  复制生成的公网链接即可"
echo ""
echo "  日志查看："
echo "    tail -f /tmp/backend.log"
echo "    tail -f /tmp/frontend.log"
echo ""
echo "  停止服务："
echo "    kill $BACKEND_PID $FRONTEND_PID"
echo ""
