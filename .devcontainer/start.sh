#!/bin/bash
set -e

WORKSPACE="/workspace"
BACKEND_DIR="$WORKSPACE/backend"
FRONTEND_DIR="$WORKSPACE/frontend"

echo "========================================"
echo " 企业仓库管理系统 - 一键启动脚本"
echo "========================================"
echo ""

echo "[1/4] 初始化 MySQL 数据库..."
service mysql start 2>/dev/null || mysqld_safe --skip-grant-tables &
sleep 3
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS warehouse_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
mysql -u root -proot warehouse_db -e "source $WORKSPACE/database-init.sql;" 2>/dev/null && echo "  数据库初始化完成" || echo "  数据库初始化跳过（已存在数据）"

echo ""
echo "[2/4] 更新后端配置（Codespaces 环境）..."
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
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.example.warehouse: DEBUG
EOF

echo ""
echo "[3/4] 启动后端服务..."
cd "$BACKEND_DIR"
nohup mvn spring-boot:run > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "  后端 PID: $BACKEND_PID"
echo "  等待后端启动..."
for i in $(seq 1 30); do
  if curl -s http://localhost:8080/api/auth/login -X POST 2>/dev/null | grep -q "code"; then
    echo "  后端已启动！"
    break
  fi
  sleep 2
done

echo ""
echo "[4/4] 启动前端服务..."
cd "$FRONTEND_DIR"
nohup npm run dev -- --host 0.0.0.0 > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  前端 PID: $FRONTEND_PID"
sleep 5

echo ""
echo "========================================"
echo " 启动完成！"
echo "========================================"
echo ""
echo " 前端地址: https://${CODESPACES_DEV_CONTAINER_HOST_NAME}-5173.preview.app.github.dev"
echo " 后端地址: https://${CODESPACES_DEV_CONTAINER_HOST_NAME}-8080.preview.app.github.dev"
echo ""
echo " 预览链接:"
echo "  浏览器打开上方 5173 端口地址即可使用"
echo ""
echo " 后台日志:"
echo "    后端: tail -f /tmp/backend.log"
echo "    前端: tail -f /tmp/frontend.log"
echo ""

cd "$WORKSPACE"
