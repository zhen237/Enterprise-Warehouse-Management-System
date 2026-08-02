# 后端 Dockerfile - 用于 Render 部署
# 多阶段构建：先用 Maven 编译打包，再用 JRE 运行

# 阶段 1：构建阶段
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 先复制 pom.xml 下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:resolve -B

# 复制源代码并打包
COPY src ./src
RUN mvn package -DskipTests -B

# 阶段 2：运行阶段
FROM eclipse-temurin:21-jre
WORKDIR /app

# 复制构建好的 JAR 包
COPY --from=build /app/target/*.jar app.jar

# 暴露端口（Render 会通过 PORT 环境变量指定）
EXPOSE 8080

# 启动命令
CMD ["java", "-jar", "app.jar"]
