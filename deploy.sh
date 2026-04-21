#!/bin/bash
# AI Todo 部署脚本

set -e

echo "=== Step 1: 修改 aitodo 服务为 8080 端口 ==="
cat > /etc/systemd/system/aitodo.service << 'EOF'
[Unit]
Description=AI Todo Procrastination Cure
After=network.target mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt
Environment=JAVA_HOME=/usr/lib/jvm/jre-21
ExecStart=/usr/bin/java -jar /opt/aitodo.jar --server.port=8080
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
echo "服务文件已创建"

echo "=== Step 2: 配置 Nginx 反向代理 ==="
cat > /etc/nginx/conf.d/aitodo.conf << 'EOF'
server {
    listen 80;
    server_name 47.109.156.71;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
    }
}
EOF
echo "Nginx 配置已创建"

echo "=== Step 3: 检查 Nginx 默认配置 ==="
# 注释掉 nginx 默认server的80监听(如果 default.conf 存在)
if [ -f /etc/nginx/conf.d/default.conf ]; then
    mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.bak
    echo "已备份 default.conf"
fi

echo "=== Step 4: 测试 Nginx 配置 ==="
nginx -t

echo "=== Step 5: 重载 Nginx ==="
systemctl reload nginx
echo "Nginx 已重载"

echo "=== Step 6: 启动 aitodo 服务 ==="
systemctl daemon-reload
systemctl enable aitodo
systemctl restart aitodo
sleep 5

echo "=== Step 7: 检查服务状态 ==="
systemctl status aitodo --no-pager | head -10

echo "=== Step 8: 检查端口 ==="
ss -tlnp | grep -E ':80 |:8080 '

echo "=== 部署完成! ==="
