# MySQL 数据库设置指南

本文档指导您将 AI Todo + 拖延症治疗器 应用从 H2 内存数据库切换到 MySQL 持久化数据库。

## 1. 安装 MySQL

### macOS (使用 Homebrew)
```bash
# 安装 MySQL
brew install mysql

# 启动 MySQL 服务
brew services start mysql

# 设置 root 密码（第一次安装后运行）
mysql_secure_installation
```

### Ubuntu/Debian
```bash
# 安装 MySQL
sudo apt update
sudo apt install mysql-server

# 启动 MySQL 服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 设置 root 密码
sudo mysql_secure_installation
```

### Windows
1. 下载 MySQL Installer: https://dev.mysql.com/downloads/installer/
2. 运行安装程序，选择 "MySQL Server"
3. 在安装过程中设置 root 密码
4. 安装完成后启动 MySQL 服务

## 2. 创建数据库和用户

### 方法A: 使用 init-db.sql 脚本
```bash
# 登录 MySQL（使用您的 root 密码）
mysql -u root -p < init-db.sql
```

### 方法B: 手动创建
```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS aitodo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户（可选，推荐用于生产环境）
CREATE USER 'aitodo_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON aitodo.* TO 'aitodo_user'@'localhost';
FLUSH PRIVILEGES;

-- 3. 使用数据库
USE aitodo;
```

## 3. 配置应用连接

### 修改配置文件
打开 `src/main/resources/application.properties`，更新以下配置：

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/aitodo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root                # 改为您的 MySQL 用户名
spring.datasource.password=your_mysql_password # 改为您的 MySQL 密码

# 如果使用创建的用户，替换为：
# spring.datasource.username=aitodo_user
# spring.datasource.password=StrongPassword123!
```

### 可选：使用环境变量（推荐用于生产环境）
```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

然后在启动应用时设置环境变量：
```bash
export DB_USERNAME=aitodo_user
export DB_PASSWORD=StrongPassword123!
mvn spring-boot:run
```

## 4. 验证连接

### 启动应用
```bash
mvn spring-boot:run
```

### 检查启动日志
在启动日志中寻找以下信息：
```
HikariPool-1 - Start completed.
```

### 测试 API
```bash
# 健康检查
curl http://localhost:8080/api/health

# 注册用户
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'

# 登录
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

## 5. 数据库维护

### 查看数据库内容
```bash
# 登录 MySQL
mysql -u root -p aitodo

# 查看表
SHOW TABLES;

# 查看用户数据
SELECT * FROM users;

# 查看任务数据
SELECT * FROM tasks;
```

### 备份数据库
```bash
# 备份整个数据库
mysqldump -u root -p aitodo > aitodo_backup_$(date +%Y%m%d).sql

# 仅备份结构
mysqldump -u root -p --no-data aitodo > aitodo_schema.sql

# 仅备份数据
mysqldump -u root -p --no-create-info aitodo > aitodo_data.sql
```

### 恢复数据库
```bash
mysql -u root -p aitodo < aitodo_backup.sql
```

## 6. 故障排除

### 问题1: 连接拒绝
**症状**: `Communications link failure` 或 `Access denied`
**解决方案**:
1. 检查 MySQL 服务是否运行
   ```bash
   brew services list | grep mysql  # macOS
   systemctl status mysql          # Linux
   ```
2. 验证用户名和密码
3. 检查防火墙设置（端口 3306 是否开放）

### 问题2: 时区错误
**症状**: `The server time zone value 'xxx' is unrecognized`
**解决方案**:
在连接 URL 中添加时区参数：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aitodo?serverTimezone=Asia/Shanghai
```

### 问题3: 公共密钥检索错误
**症状**: `Public Key Retrieval is not allowed`
**解决方案**:
在连接 URL 中添加：
```properties
?allowPublicKeyRetrieval=true&useSSL=false
```

### 问题4: 驱动类未找到
**症状**: `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`
**解决方案**:
检查 `pom.xml` 是否包含 MySQL 依赖：
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 7. 性能优化建议

### 连接池优化
```properties
# 根据负载调整连接池大小
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### MySQL 配置优化
```ini
# 在 my.cnf 中添加
[mysqld]
max_connections = 200
innodb_buffer_pool_size = 256M
innodb_log_file_size = 64M
query_cache_size = 32M
```

## 8. 回退到 H2（开发环境）

如果需要快速切换回 H2 内存数据库，修改 `application.properties`：

```properties
# 注释 MySQL 配置，启用 H2
# spring.datasource.url=jdbc:mysql://localhost:3306/aitodo...
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.url=jdbc:h2:mem:aitodo;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# 启用 H2 控制台（访问 http://localhost:8080/h2-console）
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# 使用 H2 schema
spring.sql.init.schema-locations=classpath:schema.sql
```

## 9. 生产环境部署建议

1. **使用专用数据库用户**，而不是 root
2. **设置强密码**，定期更换
3. **启用 SSL 连接**
4. **定期备份**数据库
5. **监控**数据库性能指标
6. **考虑使用**云数据库服务（AWS RDS、Google Cloud SQL、Azure Database for MySQL）

---

**支持与帮助**
- MySQL 官方文档: https://dev.mysql.com/doc/
- Spring Boot 数据库配置: https://spring.io/guides/gs/accessing-data-mysql/
- 应用问题: 查看启动日志和 `boot.log` 文件