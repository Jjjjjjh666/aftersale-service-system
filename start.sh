#!/bin/bash

echo "========================================="
echo "售后服务系统 - 快速启动脚本"
echo "========================================="

# 检查MySQL是否运行
echo ""
echo "1. 检查MySQL服务..."
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL未安装，请先安装MySQL"
    exit 1
fi

# 初始化数据库
echo ""
echo "2. 初始化数据库..."
read -p "是否需要初始化数据库？(y/n): " init_db
if [ "$init_db" = "y" ]; then
    read -p "请输入MySQL root密码: " -s mysql_pwd
    echo ""
    mysql -uroot -p"$mysql_pwd" < sql/aftersale_db.sql
    mysql -uroot -p"$mysql_pwd" < sql/service_db.sql
    echo "✅ 数据库初始化完成"
fi

# 编译项目
echo ""
echo "3. 编译项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 编译失败"
    exit 1
fi
echo "✅ 编译完成"

# 启动service模块
echo ""
echo "4. 启动service模块（端口8082）..."
cd service
mvn spring-boot:run &
SERVICE_PID=$!
cd ..
echo "✅ Service模块启动中... PID: $SERVICE_PID"

# 等待service模块启动
sleep 10

# 启动aftersale模块
echo ""
echo "5. 启动aftersale模块（端口8081）..."
cd aftersale
mvn spring-boot:run &
AFTERSALE_PID=$!
cd ..
echo "✅ Aftersale模块启动中... PID: $AFTERSALE_PID"

echo ""
echo "========================================="
echo "启动完成！"
echo "========================================="
echo "Aftersale模块: http://localhost:8081"
echo "Service模块: http://localhost:8082"
echo ""
echo "PID:"
echo "  - Service: $SERVICE_PID"
echo "  - Aftersale: $AFTERSALE_PID"
echo ""
echo "停止服务: kill $SERVICE_PID $AFTERSALE_PID"
echo "查看日志: tail -f aftersale/target/*.log"
echo "========================================="

